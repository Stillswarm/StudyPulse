package com.studypulse.feat.flashcards.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.feat.flashcards.domain.model.UserStars
import com.studypulse.feat.flashcards.domain.repository.UserStarsRepository
import kotlinx.coroutines.tasks.await

class UserStarsRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore
) : BaseFirebaseRepository(auth, db), UserStarsRepository {

    companion object {
        private const val COLLECTION_KEY = "user_stars"
    }

    fun collection() = db.collection(COLLECTION_KEY)

    override suspend fun starPack(packId: String) = runCatching {
        val docId = db.collection(COLLECTION_KEY)
            .document().id

        collection()
            .document(docId)
            .set(UserStars(id = docId, packId = packId, starredBy = requireUserId()))
            .await()

        Unit
    }

    override suspend fun unstarPack(packId: String) = runCatching {
        collection()
            .whereEqualTo("packId", packId)
            .whereEqualTo("starredBy", requireUserId())
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.reference
            ?.delete()
            ?.await()
        Unit
    }

    override suspend fun getPackIdsStarredByThisUser(limit: Long) = runCatching {
        collection()
            .whereEqualTo("userId", requireUserId())
            .limit(limit)
            .get()
            .await()
            .toObjects<UserStars>()
    }

    override suspend fun hasUserStarred(packId: String) = runCatching {
        collection()
            .whereEqualTo("userId", requireUserId())
            .whereEqualTo("packId", packId)
            .limit(1)
            .get()
            .await()
            .isEmpty
            .not()
    }
}