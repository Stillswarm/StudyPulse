package com.studypulse.app.feat.user.data

import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.feat.user.domain.User
import com.studypulse.app.feat.user.domain.UserRepository
import kotlinx.coroutines.tasks.await

class FirebaseUserRepositoryImpl(
    private val db: FirebaseFirestore
) : UserRepository {
    override suspend fun addUser(user: User) =
        try {
            db.collection("users")
                .add(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    // todo
    override suspend fun deleteUser(user: User) = Result.success(Unit)

override suspend fun getUserById(id: String) =
    try {
        val docSnap = db.collection("users")
            .document(id)
            .get()
            .await()

        if (docSnap.exists()) {
            val user = docSnap.toObject(User::class.java)
            Result.success(user)
        } else {
            Result.failure(Exception("User not found"))
        }
    } catch (e: Exception) {
        Result.failure(Exception("Failed to fetch user"))
    }
}