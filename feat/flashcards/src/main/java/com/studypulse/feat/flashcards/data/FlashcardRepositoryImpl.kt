package com.studypulse.feat.flashcards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FlashcardRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    private val frRepository: FlashcardReviewRepository,
) : BaseFirebaseRepository(auth, db), FlashcardRepository {

    private companion object {
        const val FLASHCARDS_COLLECTION = "flashcards"
        const val FIRESTORE_BATCH_LIMIT = 500

        // Firestore `whereIn` accepts at most 10 values per query.
        const val WHERE_IN_LIMIT = 10
    }

    private fun flashcardsCollection() = userCollection(FLASHCARDS_COLLECTION)

    override suspend fun upsert(flashcard: Flashcard): Result<Unit> = runCatching {
        val collection = flashcardsCollection()
        val isNew = flashcard.id.isBlank()
        val docId = flashcard.id.ifBlank { collection.document().id }
        val userId = requireUserId()

        collection.document(docId)
            .set(
                flashcard.copy(
                    id = docId,
                    ownerId = userId,
                    updatedAt = System.currentTimeMillis()
                ).toDto()
            )
            .await()

        // A new card needs a review state so it becomes schedulable; it is due
        // immediately (dueDate = now) so it shows up as a new card in the queue.
        // Edits leave the existing review state untouched.
        if (isNew) {
            frRepository.upsert(
                FlashcardReviewState(
                    cardId = docId,
                    packId = flashcard.packId,
                    userId = userId,
                    dueDate = System.currentTimeMillis(),
                )
            ).getOrThrow()
        }

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

    override suspend fun getByIds(ids: List<String>): Result<Map<String, Flashcard>> =
        runCatching {
            val distinctIds = ids.distinct()
            if (distinctIds.isEmpty()) return@runCatching emptyMap()

            val collection = flashcardsCollection()
            coroutineScope {
                distinctIds.chunked(WHERE_IN_LIMIT)
                    .map { chunk ->
                        async {
                            collection
                                .whereIn(FieldPath.documentId(), chunk)
                                .get()
                                .await()
                                .toObjects<FlashcardDto>()
                                .map { it.toDomain() }
                        }
                    }
                    .awaitAll()
                    .flatten()
                    .associateBy { it.id }
            }
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

    override suspend fun getAllByPackIdAcrossOwners(packId: String): Result<List<Flashcard>> =
        runCatching {
            db.collectionGroup(FLASHCARDS_COLLECTION)
                .whereEqualTo("packId", packId)
                .get()
                .await()
                .toObjects<FlashcardDto>()
                .map { it.toDomain() }
        }

    override suspend fun delete(flashcard: Flashcard): Result<Unit> = runCatching {
        flashcardsCollection()
            .document(flashcard.id)
            .delete()
            .await()
    }

    override suspend fun deleteAllByPackId(packId: String): Result<Unit> = runCatching {
        val cardDocs = flashcardsCollection()
            .whereEqualTo("packId", packId)
            .get()
            .await()
            .documents

        // Firestore caps a single batch at 500 ops, so chunk to stay under
        // the limit even when a pack contains thousands of cards.
        cardDocs.chunked(FIRESTORE_BATCH_LIMIT).forEach { chunk ->
            db.runBatch { batch ->
                chunk.forEach { batch.delete(it.reference) }
            }.await()
        }

    }
}
