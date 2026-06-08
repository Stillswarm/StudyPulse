package com.studypulse.feat.flashcards.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
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

        // The card's `public` flag must mirror its pack so cross-owner reads can
        // be authorized purely by a query filter. Resolve it from the (always
        // owner-local) pack document at write time.
        val packIsPublic = db.collection("users/$userId/flashcardPacks")
            .document(flashcard.packId)
            .get()
            .await()
            .getBoolean("public") ?: false

        collection.document(docId)
            .set(
                flashcard.copy(
                    id = docId,
                    ownerId = userId,
                    public = packIsPublic,
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
        // Fast path: the user's own card lives under their subtree.
        flashcardsCollection()
            .document(id)
            .get()
            .await()
            .toObject(FlashcardDto::class.java)
            ?.toDomain()
        // Fallback: a card from someone else's public pack lives under that
        // owner's subtree, so it can only be reached via a collection-group
        // read constrained to public == true (the shape the rules authorize).
            ?: db.collectionGroup(FLASHCARDS_COLLECTION)
                .whereEqualTo("id", id)
                .whereEqualTo("public", true)
                .limit(1)
                .get()
                .await()
                .toObjects<FlashcardDto>()
                .firstOrNull()
                ?.toDomain()
            ?: throw NoSuchElementException("Flashcard not found")
    }

    /**
     * Resolves cards by id, spanning both the user's own cards and cards from
     * other owners' public packs (the study queue mixes the two). Firestore
     * security rules are not filters, so a single collection-group `whereIn`
     * can't be authorized for this mix. Instead we run two queries per chunk:
     *
     *  - the user's own subcollection (owner rule covers it — private or not),
     *  - a collection-group read constrained to `public == true` (the only
     *    shape the rules can authorize for cards owned by someone else).
     *
     * Results are merged and de-duplicated by id. Cards owned by others that
     * are NOT public are intentionally unreachable. The public branch is
     * best-effort: if its collection-group index isn't deployed yet, owned
     * cards still resolve from the first branch.
     */
    override suspend fun getByIds(ids: List<String>): Result<Map<String, Flashcard>> =
        runCatching {
            val distinctIds = ids.distinct()
            if (distinctIds.isEmpty()) return@runCatching emptyMap()

            val ownCollection = flashcardsCollection()

            coroutineScope {
                distinctIds.chunked(WHERE_IN_LIMIT)
                    .map { chunk ->
                        async {
                            val own = async {
                                ownCollection
                                    .whereIn("id", chunk)
                                    .get()
                                    .await()
                                    .toObjects<FlashcardDto>()
                                    .map { it.toDomain() }
                            }
                            val public = async {
                                runCatching {
                                    db.collectionGroup(FLASHCARDS_COLLECTION)
                                        .whereIn("id", chunk)
                                        .whereEqualTo("public", true)
                                        .get()
                                        .await()
                                        .toObjects<FlashcardDto>()
                                        .map { it.toDomain() }
                                }.getOrElse {
                                    // Best-effort: owned cards still resolve from
                                    // the first branch. A missing collection-group
                                    // index surfaces here (with a console link).
                                    Log.w("app", "getByIds public branch failed", it)
                                    emptyList()
                                }
                            }
                            own.await() + public.await()
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

    // Used to browse a pack the user does not own, so it is scoped to public
    // cards — the only shape the security rules can authorize across owners.
    override suspend fun getAllByPackIdAcrossOwners(packId: String): Result<List<Flashcard>> =
        runCatching {
            db.collectionGroup(FLASHCARDS_COLLECTION)
                .whereEqualTo("packId", packId)
                .whereEqualTo("public", true)
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
