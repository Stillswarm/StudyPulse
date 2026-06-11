package com.studypulse.feat.flashcards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.model.FlashcardPackDto
import com.studypulse.feat.flashcards.domain.model.PackPage
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FlashcardPackRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
) : BaseFirebaseRepository(auth, db), FlashcardPackRepository {

    companion object {
        private const val FLASHCARD_PACK_COLLECTION_KEY = "flashcardPacks"
        private const val FLASHCARDS_COLLECTION_KEY = "flashcards"
        private const val FIRESTORE_BATCH_LIMIT = 500
        private const val PUBLIC_BACKFILL_MARKER = "flashcardsPublicBackfilledV1"
    }

    private fun flashcardPacksCollection() = userCollection(FLASHCARD_PACK_COLLECTION_KEY)

    private fun flashcardsCollection() = userCollection(FLASHCARDS_COLLECTION_KEY)

    override suspend fun upsert(fcp: FlashcardPack): Result<String> = runCatching {
        val collection = flashcardPacksCollection()
        val now = System.currentTimeMillis()
        val isNew = fcp.id.isBlank()
        val docId = fcp.id.ifBlank { collection.document().id }

        collection.document(docId)
            .set(
                fcp.copy(
                    id = docId,
                    ownerId = requireUserId(),
                    updatedAt = now,
                    createdAt = if (isNew) now else fcp.createdAt
                ).toDto()
            )
            .await()

        // Keep the denormalized `public` flag on this pack's cards in sync with
        // the pack. No-op for a brand-new pack (no cards yet); on a visibility
        // edit it propagates the change so cross-owner reads stay correct.
        if (!isNew) syncCardVisibility(docId, fcp.public)

        docId
    }

    /**
     * Deletes the pack document only. Callers MUST delete the cards inside
     * the pack separately (see `DeleteFlashcardPackUseCase`);
     */
    override suspend fun delete(fcp: FlashcardPack): Result<Unit> = runCatching {
        flashcardPacksCollection()
            .document(fcp.id)
            .delete()
            .await()
    }

    override suspend fun getById(id: String): Result<FlashcardPack> = runCatching {
        val ownDoc = flashcardPacksCollection().document(id).get().await()
        if (ownDoc.exists()) {
            return@runCatching ownDoc.toObject(FlashcardPackDto::class.java)
                ?.toDomain()
                ?: throw NoSuchElementException("Failed to deserialize pack $id")
        }

        db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("id", id)
            .whereEqualTo("public", true)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(FlashcardPackDto::class.java)
            ?.toDomain()
            ?: throw NoSuchElementException("No such pack found")
    }

    override suspend fun getAllForOwner(ownerId: String): Result<List<FlashcardPack>> =
        runCatching {
            db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
                .whereEqualTo("ownerId", ownerId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects<FlashcardPackDto>()
                .map { it.toDomain() }
        }

    override fun getAllForOwnerFlow(ownerId: String): Result<Flow<List<FlashcardPack>>> =
        runCatching {
            db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
                .whereEqualTo("ownerId", ownerId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .snapshotFlow { doc ->
                    doc.toObject<FlashcardPackDto>()?.toDomain()
                }
        }

    override suspend fun getNForOwner(
        ownerId: String,
        n: Long,
    ): Result<List<FlashcardPack>> = runCatching {
        db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("ownerId", ownerId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(n)
            .get()
            .await()
            .toObjects<FlashcardPackDto>()
            .map { it.toDomain() }
    }

    override fun getNForOwnerFlow(
        ownerId: String,
        n: Long,
    ) = runCatching {
        db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("ownerId", ownerId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(n)
            .snapshotFlow { doc ->
                doc.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    override suspend fun getAllForThisUser() = runCatching {
        flashcardPacksCollection()
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects<FlashcardPackDto>()
            .map { it.toDomain() }
    }

    override fun getAllForThisUserFlow() = runCatching {
        flashcardPacksCollection()
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .snapshotFlow {
                it.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    override suspend fun getNForThisUser(n: Long, cursor: DocumentSnapshot?) = runCatching {
        var query = flashcardPacksCollection()
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(n)
        cursor?.let { query = query.startAfter(it) }

        val snapshot = query.get().await()
        val items = snapshot.toObjects<FlashcardPackDto>().map { it.toDomain() }
        PackPage(
            items = items,
            nextCursor = snapshot.documents.lastOrNull(),
            endReached = items.size < n,
        )
    }

    override fun getNForThisUserFlow(n: Long) = runCatching {
        flashcardPacksCollection()
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(n)
            .snapshotFlow {
                it.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    override suspend fun getPopularPacks(limit: Long, cursor: DocumentSnapshot?) = runCatching {
        var query = db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("public", true)
            .orderBy("starCount", Query.Direction.DESCENDING)
            .limit(limit)
        cursor?.let { query = query.startAfter(it) }

        val snapshot = query.get().await()
        val userId = requireUserId()
        val items = snapshot
            .toObjects<FlashcardPackDto>()
            .filter { it.ownerId != userId }
            .map { it.toDomain() }
        PackPage(
            items = items,
            nextCursor = snapshot.documents.lastOrNull(),
            // The raw page size determines whether more rows exist on the
            // server; `items` may be shorter after the owner filter.
            endReached = snapshot.size() < limit,
        )
    }

    override fun getPopularPacksFlow(limit: Long) = runCatching {
        db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("public", true)
            .whereNotEqualTo("ownerId", requireUserId())
            .orderBy("starCount", Query.Direction.DESCENDING)
            .limit(limit)
            .snapshotFlow {
                it.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    override suspend fun backfillPublicFlags(): Result<Unit> = runCatching {
        val userDoc = userDocument()
        val alreadyDone = userDoc.get().await().getBoolean(PUBLIC_BACKFILL_MARKER) == true
        if (alreadyDone) return@runCatching

        flashcardPacksCollection().get().await().documents.forEach { packDoc ->
            syncCardVisibility(packDoc.id, packDoc.getBoolean("public") ?: false)
        }

        userDoc.set(mapOf(PUBLIC_BACKFILL_MARKER to true), SetOptions.merge()).await()
    }

    /**
     * Stamps every card in [packId] (under the current user's subtree) with
     * [isPublic]. Batched to respect Firestore's 500-op limit.
     */
    private suspend fun syncCardVisibility(packId: String, isPublic: Boolean) {
        val cardDocs = flashcardsCollection()
            .whereEqualTo("packId", packId)
            .get()
            .await()
            .documents

        cardDocs.chunked(FIRESTORE_BATCH_LIMIT).forEach { chunk ->
            db.runBatch { batch ->
                chunk.forEach { batch.update(it.reference, "public", isPublic) }
            }.await()
        }
    }
}