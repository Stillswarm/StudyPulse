package com.studypulse.feat.flashcards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FlashcardRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
) : BaseFirebaseRepository(auth, db), FlashcardRepository {

    private fun flashcardsCollection() = userCollection("flashcards")

    override suspend fun upsert(flashcard: Flashcard): Result<Unit> = runCatching {
        val collection = flashcardsCollection()
        val docId = flashcard.id.ifBlank { collection.document().id }
        collection.document(docId)
            .set(flashcard.copy(id = docId, ownerId = requireUserId()).toDto())
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

    override suspend fun getAllByPackIdFlow(packId: String): Result<Flow<List<Flashcard>>> =
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

    override suspend fun getNRandom(n: Long): Result<List<Flashcard>> = runCatching {
        flashcardsCollection()
            .limit(n)
            .get()
            .await()
            .toObjects(FlashcardDto::class.java)
            .map { it.toDomain() }
    }
}
