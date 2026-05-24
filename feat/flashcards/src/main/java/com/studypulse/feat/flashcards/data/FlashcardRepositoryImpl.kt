package com.studypulse.feat.flashcards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardCursors
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap

class FlashcardRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    private val frRepository: FlashcardReviewRepository,
) : BaseFirebaseRepository(auth, db), FlashcardRepository {

    override var flashcardPageCache: MutableMap<String, FlashcardPage> = ConcurrentHashMap()

    private fun flashcardsCollection() = userCollection("flashcards")

    override suspend fun upsert(flashcard: Flashcard): Result<Unit> = runCatching {
        val collection = flashcardsCollection()
        val docId = flashcard.id.ifBlank { collection.document().id }
        collection.document(docId)
            .set(
                flashcard.copy(
                    id = docId,
                    ownerId = requireUserId(),
                    updatedAt = System.currentTimeMillis()
                ).toDto()
            )
            .await()
    }

    override suspend fun getById(id: String): Result<Flashcard> = runCatching {
        flashcardsCollection()
            .document(id)
            .get()
            .await()
            .toObject(FlashcardDto::class.java)
            ?.toDomain()
            ?: throw NoSuchElementException("Flashcard not found")
    }

    override suspend fun getAllByOwner(ownerId: String): Result<List<Flashcard>> = runCatching {
        flashcardsCollection()
            .whereEqualTo("ownerId", ownerId)
            .get()
            .await()
            .toObjects(FlashcardDto::class.java)
            .map { it.toDomain() }
    }

    override suspend fun getAllByOwnerFlow(ownerId: String): Result<Flow<List<Flashcard>>> =
        runCatching {
            flashcardsCollection()
                .whereEqualTo("ownerId", ownerId)
                .snapshotFlow { doc ->
                    doc.toObject(FlashcardDto::class.java)?.toDomain()
                }
        }

    override suspend fun getAllByPackId(packId: String): Result<List<Flashcard>> = runCatching {
        flashcardsCollection()
            .whereEqualTo("packId", packId)
            .get()
            .await()
            .toObjects(FlashcardDto::class.java)
            .map { it.toDomain() }
    }

    override fun getAllByPackIdFlow(packId: String): Result<Flow<List<Flashcard>>> =
        runCatching {
            flashcardsCollection()
                .whereEqualTo("packId", packId)
                .snapshotFlow { doc ->
                    doc.toObject(FlashcardDto::class.java)?.toDomain()
                }
        }

    override suspend fun delete(flashcard: Flashcard): Result<Unit> = runCatching {
        flashcardsCollection()
            .document(flashcard.id)
            .delete()
            .await()
    }

    /*
        1. take all cards that are due today, ordered by most overdue first
        2. take new cards, cards that haven't been seen yet
        3. take cards that have been seen and are due later
     */
    override suspend fun getNRandomFromAcrossPacks(
        n: Long,
        cursors: FlashcardCursors
    ): Result<FlashcardPage> = runCatching {
        val now = System.currentTimeMillis()

        val collection = flashcardsCollection()

        // Priority 1: due/overdue
        val dueNowSnap = collection
            .whereLessThanOrEqualTo("dueDate", now)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .limit(n)
            .let { if (cursors.lastDueNow != null) it.startAfter(cursors.lastDueNow) else it }
            .get().await()

        val dueNow = dueNowSnap.toObjects<FlashcardDto>().map { it.toDomain() }

        if (dueNow.size >= n) return@runCatching FlashcardPage(
            cards = dueNow.take(n.toInt()).toSm2Flashcards(),
            cursors = cursors.copy(lastDueNow = dueNowSnap.documents.lastOrNull())
        )

        // Priority 2: new, never reviewed
        var rem = n - dueNow.size
        val newCardsSnap = collection
            .whereEqualTo("read", false)
            .whereGreaterThan("dueDate", now)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .limit(rem)
            .let { if (cursors.lastNewCard != null) it.startAfter(cursors.lastNewCard) else it }
            .get().await()

        val newCards = newCardsSnap.toObjects<FlashcardDto>().map { it.toDomain() }

        val tot = dueNow.size + newCards.size
        if (tot >= n) return@runCatching FlashcardPage(
            cards = (dueNow + newCards).toSm2Flashcards(),
            cursors = cursors.copy(
                lastDueNow = dueNowSnap.documents.lastOrNull(),
                lastNewCard = newCardsSnap.documents.lastOrNull()
            )
        )

        // Priority 3: reviewed, not yet due
        rem = n - tot
        val dueLaterSnap = collection
            .whereEqualTo("read", true)
            .whereGreaterThan("dueDate", now)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .limit(rem)
            .let { if (cursors.lastDueLater != null) it.startAfter(cursors.lastDueLater) else it }
            .get().await()

        val dueLater = dueLaterSnap.toObjects<FlashcardDto>().map { it.toDomain() }

        FlashcardPage(
            cards = (dueNow + newCards + dueLater).toSm2Flashcards(),
            cursors = cursors.copy(
                lastDueNow = dueNowSnap.documents.lastOrNull(),
                lastNewCard = newCardsSnap.documents.lastOrNull(),
                lastDueLater = dueLaterSnap.documents.lastOrNull()
            )
        )
    }

    override suspend fun getNRandomFromSamePack(
        n: Long,
        packId: String,
        cursors: FlashcardCursors
    ): Result<FlashcardPage> = runCatching {
        val now = System.currentTimeMillis()

        // Priority 1: due/overdue
        val dueNowSnap = flashcardsCollection()
            .whereEqualTo("packId", packId)
            .whereLessThanOrEqualTo("dueDate", now)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .limit(n)
            .let { if (cursors.lastDueNow != null) it.startAfter(cursors.lastDueNow) else it }
            .get().await()                          // ← hold QuerySnapshot

        val dueNow = dueNowSnap.toObjects<FlashcardDto>().map { it.toDomain() }

        if (dueNow.size >= n) return@runCatching FlashcardPage(
            cards = dueNow.take(n.toInt()).toSm2Flashcards(),
            cursors = cursors.copy(lastDueNow = dueNowSnap.documents.lastOrNull())
        ).also { newPage ->
            updateCache(packId, newPage)
        }

        // Priority 2: new, never reviewed
        var rem = n - dueNow.size
        val newCardsSnap = flashcardsCollection()
            .whereEqualTo("packId", packId)
            .whereEqualTo("read", false)
            .whereGreaterThan("dueDate", now)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .limit(rem)
            .let { if (cursors.lastNewCard != null) it.startAfter(cursors.lastNewCard) else it }
            .get().await()

        val newCards = newCardsSnap.toObjects<FlashcardDto>().map { it.toDomain() }


        val tot = dueNow.size + newCards.size
        if (tot >= n) return@runCatching FlashcardPage(
            cards = (dueNow + newCards).toSm2Flashcards(),
            cursors = cursors.copy(
                lastDueNow = dueNowSnap.documents.lastOrNull(),
                lastNewCard = newCardsSnap.documents.lastOrNull()
            )
        ).also { newPage -> updateCache(packId, newPage) }

        // Priority 3: reviewed, not yet due
        rem = n - tot
        val dueLaterSnap = flashcardsCollection()
            .whereEqualTo("packId", packId)
            .whereEqualTo("read", true)
            .whereGreaterThan("dueDate", now)
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .limit(rem)
            .let { if (cursors.lastDueLater != null) it.startAfter(cursors.lastDueLater) else it }
            .get().await()

        val dueLater = dueLaterSnap.toObjects<FlashcardDto>().map { it.toDomain() }


        FlashcardPage(
            cards = (dueNow + newCards + dueLater).toSm2Flashcards(),
            cursors = cursors.copy(
                lastDueNow = dueNowSnap.documents.lastOrNull(),
                lastNewCard = newCardsSnap.documents.lastOrNull(),
                lastDueLater = dueLaterSnap.documents.lastOrNull()
            )
        ).also { newPage ->
            updateCache(packId, newPage)
        }
    }

    /**
     * Fans out one review-state fetch per card in parallel and packs each
     * Flashcard with its review state. Cards without a stored review state
     * fall back to a default [FlashcardReviewState] (i.e. "never reviewed").
     */
    private suspend fun List<Flashcard>.toSm2Flashcards(): List<Sm2Flashcard> = coroutineScope {
        map { card ->
            async {
                val reviewState = frRepository.get(card.id)
                    .getOrElse { FlashcardReviewState() }
                Sm2Flashcard(flashcard = card, reviewState = reviewState)
            }
        }.awaitAll()
    }

    // compute() makes the process atomic and concurrency-safe
    private fun updateCache(packId: String, newPage: FlashcardPage) {
        flashcardPageCache.compute(packId) { _, existing ->
            existing?.copy(
                cards = existing.cards + newPage.cards,
                cursors = newPage.cursors,
            ) ?: newPage
        }
    }
}
