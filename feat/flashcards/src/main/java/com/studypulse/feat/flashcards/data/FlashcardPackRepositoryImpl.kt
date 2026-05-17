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

    private fun flashcardPacksCollection() = userCollection("flashcardPacks")

    override suspend fun upsert(fcp: FlashcardPack): Result<String> = runCatching {
        val collection = flashcardPacksCollection()
        val docId = fcp.id.ifBlank { collection.document().id }
        collection.document(docId)
            .set(
                fcp.copy(
                    id = docId,
                    ownerId = requireUserId(),
                    updatedAt = System.currentTimeMillis()
                ).toDto()
            )
            .await()
        docId
    }

    override suspend fun delete(fcp: FlashcardPack): Result<Unit> = runCatching {
        flashcardPacksCollection().document(fcp.id).delete().await()
    }

    override suspend fun getById(id: String): Result<FlashcardPack> = runCatching {
        flashcardPacksCollection()
            .document(id)
            .get()
            .await()
            .toObject(FlashcardPackDto::class.java)
            ?.toDomain()
            ?: throw NoSuchElementException("No such pack found")
    }

    override suspend fun getAllForOwner(ownerId: String): Result<List<FlashcardPack>> =
        runCatching {
            flashcardPacksCollection()
                .whereEqualTo("ownerId", ownerId)
                .get()
                .await()
                .toObjects<FlashcardPackDto>()
                .map { it.toDomain() }
        }

    override fun getAllForOwnerFlow(ownerId: String): Result<Flow<List<FlashcardPack>>> =
        runCatching {
            flashcardPacksCollection()
                .whereEqualTo("ownerId", ownerId)
                .snapshotFlow { doc ->
                    doc.toObject<FlashcardPackDto>()?.toDomain()
                }
        }

    override suspend fun getNForOwner(
        ownerId: String,
        n: Long,
    ): Result<List<FlashcardPack>> = runCatching {
        flashcardPacksCollection()
            .whereEqualTo("ownerId", ownerId)
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
        flashcardPacksCollection()
            .whereEqualTo("ownerId", ownerId)
            .limit(n)
            .snapshotFlow { doc ->
                doc.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    override suspend fun getAllForThisUser() = runCatching {
        flashcardPacksCollection()
            .whereEqualTo("id", requireUserId())
            .get()
            .await()
            .toObjects<FlashcardPackDto>()
            .map { it.toDomain() }
    }

    override fun getAllForThisUserFlow() = runCatching {
        flashcardPacksCollection()
            .whereEqualTo("id", requireUserId())
            .snapshotFlow {
                it.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    override suspend fun getNForThisUser(n: Long)= runCatching {
        flashcardPacksCollection()
            .whereEqualTo("id", requireUserId())
            .limit(n)
            .get()
            .await()
            .toObjects<FlashcardPackDto>()
            .map { it.toDomain() }
    }

    override fun getNForThisUserFlow(n: Long) = runCatching {
        flashcardPacksCollection()
            .whereEqualTo("id", requireUserId())
            .limit(n)
            .snapshotFlow {
                it.toObject<FlashcardPackDto>()?.toDomain()
            }
    }

    // TODO: do this in more idiomatic pattern
    override suspend fun getPopularPacks(limit: Long) = runCatching {
        flashcardPacksCollection()
            .whereNotEqualTo("ownerId", requireUserId())
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
            .toObjects<FlashcardPackDto>()
            .map { it.toDomain() }
    }
}