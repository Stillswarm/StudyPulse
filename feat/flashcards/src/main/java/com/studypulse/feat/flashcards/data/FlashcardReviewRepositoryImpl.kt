package com.studypulse.feat.flashcards.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewStateDto
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await

class FlashcardReviewRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
) : BaseFirebaseRepository(auth, db), FlashcardReviewRepository {

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
    }

    override suspend fun upsertMany(frsList: List<FlashcardReviewState>): Result<Unit> =
        runCatching {
            val collection = frsCollection()
            val userId = requireUserId()

            supervisorScope {
                frsList.chunked(500)
                    .map { chunk ->
                        async {
                            val batch = db.batch()
                            for (frs in chunk) {
                                val id = frs.id.ifBlank { collection.document().id }
                                batch.set(
                                    collection.document(id),
                                    frs.copy(id = id, userId = userId)
                                )
                            }
                            batch.commit().await()
                        }
                    }.awaitAll()
            }
        }

    override suspend fun delete(frs: FlashcardReviewState): Result<Unit> = runCatching {
        frsCollection().document(frs.id)
            .delete()
            .await()
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
}