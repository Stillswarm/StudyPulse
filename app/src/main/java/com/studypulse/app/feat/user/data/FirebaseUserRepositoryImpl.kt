package com.studypulse.app.feat.user.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.studypulse.app.feat.user.domain.UserRepository
import com.studypulse.app.feat.user.domain.model.User
import com.studypulse.app.feat.user.domain.model.UserDto
import com.studypulse.app.feat.user.domain.model.toDomain
import com.studypulse.app.feat.user.domain.model.toDto
import kotlinx.coroutines.tasks.await

class FirebaseUserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
//    private val searchClient: SearchClient,
) : UserRepository {

    override suspend fun fetchCurrentUser() =
        kotlin.runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            db.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject<UserDto>()
                ?.toDomain()
        }.onFailure {
            Log.d("tag", "fetchCurrentUser: ${it.message}")
        }

    override suspend fun addUser(user: User) = try {
        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
        db.collection("users").document(userId)
            .set(user.toDto(), SetOptions.merge()) // This will merge with existing data
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
                val user = docSnap.toObject(UserDto::class.java)?.toDomain()
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to fetch user"))
        }

    override suspend fun updateName(newName: String) =
        kotlin.runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            db.collection("users").document(userId).update("name", newName).await()
            Unit
        }

    override suspend fun updateInstitution(newInstitution: String) =
        kotlin.runCatching {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            db.collection("users").document(userId).update("institution", newInstitution).await()
            Unit
        }
}