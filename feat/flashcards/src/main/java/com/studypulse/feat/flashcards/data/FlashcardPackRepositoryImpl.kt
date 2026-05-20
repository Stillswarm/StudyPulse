package com.studypulse.feat.flashcards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.model.FlashcardPackDto
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FlashcardPackRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
) : BaseFirebaseRepository(auth, db), FlashcardPackRepository {

    companion object {
        private const val FLASHCARD_PACK_COLLECTION_KEY = "flashcardPacks"
        private const val FLASHCARD_COLLECTION_KEY = "flashcards"
    }

    private fun flashcardPacksCollection() = userCollection(FLASHCARD_PACK_COLLECTION_KEY)
    private fun flashcardsCollection() = userCollection(FLASHCARD_COLLECTION_KEY)

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
        docId
    }

    override suspend fun delete(fcp: FlashcardPack): Result<Unit> = runCatching {
        val packDocRef = flashcardPacksCollection().document(fcp.id)
        val cardDocs = flashcardsCollection()
            .whereEqualTo("packId", fcp.id)
            .get()
            .await()
            .documents

        // Note: Firestore batches are limited to 500 operations.
        // If a single pack ever holds 500+ cards, this needs chunking.
        db.runBatch { batch ->
            cardDocs.forEach { batch.delete(it.reference) }
            batch.delete(packDocRef)
        }.await()
    }

    override suspend fun getById(id: String): Result<FlashcardPack> = runCatching {
        db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("id", id)
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

    override suspend fun getNForThisUser(n: Long) = runCatching {
        flashcardPacksCollection()
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(n)
            .get()
            .await()
            .toObjects<FlashcardPackDto>()
            .map { it.toDomain() }
    }

    override fun getNForThisUserFlow(n: Long) = runCatching {
        flashcardPacksCollection()
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(n)
            .snapshotFlow {
                it.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    // TODO: do this in more idiomatic pattern
    override suspend fun getPopularPacks(limit: Long) = runCatching {
        db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("isPublic", true)
            .whereNotEqualTo("ownerId", requireUserId())
            .orderBy("ownerId")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
            .toObjects<FlashcardPackDto>()
            .map { it.toDomain() }
    }

    override fun getPopularPacksFlow(limit: Long) = runCatching {
        db.collectionGroup(FLASHCARD_PACK_COLLECTION_KEY)
            .whereEqualTo("isPublic", true)
            .whereNotEqualTo("ownerId", requireUserId())
            .orderBy("ownerId")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(limit)
            .snapshotFlow {
                it.toObject<FlashcardPackDto>()?.toDomain()
            }
    }
}