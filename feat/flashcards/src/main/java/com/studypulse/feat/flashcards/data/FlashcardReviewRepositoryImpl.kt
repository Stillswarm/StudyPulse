package com.studypulse.feat.flashcards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.FlashcardDataSignal
import com.studypulse.feat.flashcards.domain.FlashcardTopic
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewStateDto
import com.studypulse.feat.flashcards.domain.model.ReviewStatePage
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await

class FlashcardReviewRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    private val signal: FlashcardDataSignal,
) : BaseFirebaseRepository(auth, db), FlashcardReviewRepository {

    private companion object {
        const val FIRESTORE_BATCH_LIMIT = 500

        // Firestore `whereIn` accepts at most 10 values per query.
        const val WHERE_IN_LIMIT = 10
    }

    private fun frsCollection() = userCollection("flashcardReviewStates")

    override suspend fun upsert(frs: FlashcardReviewState): Result<Unit> = runCatching {
        val docId = frs.id.ifBlank { frsCollection().document().id }
        frsCollection().document(docId)
            .set(
                frs.copy(
                    id = docId,
                    userId = requireUserId()
                ).toDto()
            )
            .await()
        signal.bump(FlashcardTopic.REVIEWS)
    }

    override suspend fun upsertMany(frsList: List<FlashcardReviewState>): Result<Unit> =
        runCatching {
            val collection = frsCollection()
            val userId = requireUserId()

            supervisorScope {
                frsList.chunked(FIRESTORE_BATCH_LIMIT)
                    .map { chunk ->
                        async {
                            val batch = db.batch()
                            for (frs in chunk) {
                                val id = frs.id.ifBlank { collection.document().id }
                                batch.set(
                                    collection.document(id),
                                    frs.copy(id = id, userId = userId).toDto()
                                )
                            }
                            batch.commit().await()
                        }
                    }.awaitAll()
            }
            signal.bump(FlashcardTopic.REVIEWS)
        }

    override suspend fun delete(frs: FlashcardReviewState): Result<Unit> = runCatching {
        frsCollection().document(frs.id)
            .delete()
            .await()
        signal.bump(FlashcardTopic.REVIEWS)
    }

    override suspend fun get(
        cardId: String
    ) = runCatching {
        frsCollection()
            .whereEqualTo("cardId", cardId)
            .whereEqualTo("userId", requireUserId())
            .limit(1)
            .get()
            .await()
            .toObjects<FlashcardReviewStateDto>()
            .firstOrNull()
            ?.toDomain()
            ?: throw NoSuchElementException("review state not found")
    }

    override suspend fun getByCardIds(
        cardIds: List<String>
    ): Result<Map<String, FlashcardReviewState>> = runCatching {
        val distinctIds = cardIds.distinct()
        if (distinctIds.isEmpty()) return@runCatching emptyMap()

        val collection = frsCollection()
        coroutineScope {
            distinctIds.chunked(WHERE_IN_LIMIT)
                .map { chunk ->
                    async {
                        collection
                            .whereIn("cardId", chunk)
                            .get()
                            .await()
                            .toObjects<FlashcardReviewStateDto>()
                            .map { it.toDomain() }
                    }
                }
                .awaitAll()
                .flatten()
                .associateBy { it.cardId }
        }
    }

    override suspend fun getDueReviewStates(
        n: Long,
        packId: String?,
        cursor: DocumentSnapshot?,
    ) = queryByDueDate(
        n = n,
        packId = packId,
        cursor = cursor,
    ) { whereLessThanOrEqualTo("dueDate", System.currentTimeMillis()) }

    override suspend fun getUpcomingReviewStates(
        n: Long,
        packId: String?,
        cursor: DocumentSnapshot?,
    ) = queryByDueDate(
        n = n,
        packId = packId,
        cursor = cursor,
    ) { whereGreaterThan("dueDate", System.currentTimeMillis()) }

    /**
     * Shared shape for both queue pages: optional pack scope, the caller's
     * dueDate predicate, ascending dueDate ordering, and cursor-based paging.
     * The collection is already user-scoped via [userCollection], so no extra
     * userId filter is needed.
     */
    private suspend fun queryByDueDate(
        n: Long,
        packId: String?,
        cursor: DocumentSnapshot?,
        dueDatePredicate: Query.() -> Query,
    ): Result<ReviewStatePage> = runCatching {
        val snapshot = frsCollection()
            .let { if (packId != null) it.whereEqualTo("packId", packId) else it }
            .dueDatePredicate()
            .orderBy("dueDate", Query.Direction.ASCENDING)
            .let { if (cursor != null) it.startAfter(cursor) else it }
            .limit(n)
            .get()
            .await()

        ReviewStatePage(
            states = snapshot.toObjects<FlashcardReviewStateDto>().map { it.toDomain() },
            lastDoc = snapshot.documents.lastOrNull(),
        )
    }
}