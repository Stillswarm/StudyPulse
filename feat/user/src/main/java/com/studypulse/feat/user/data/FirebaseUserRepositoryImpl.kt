package com.studypulse.feat.user.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.core.user.model.User
import com.studypulse.core.user.model.UserDto
import com.studypulse.core.user.model.toDomain
import com.studypulse.core.user.model.toDto
import com.studypulse.core.user.repository.UserRepository
import kotlinx.coroutines.tasks.await

class FirebaseUserRepositoryImpl(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
) : BaseFirebaseRepository(auth, db), UserRepository {

    override suspend fun fetchCurrentUser() =
        runCatching {
            userDocument()
                .get()
                .await()
                .toObject<UserDto>()
                ?.toDomain()
        }.onFailure {
            Log.d("tag", "fetchCurrentUser: ${it.message}")
        }

    override suspend fun addUser(user: User) = try {
        userDocument()
            .set(user.toDto(), SetOptions.merge())
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
        runCatching {
            userDocument().update("name", newName).await()
            Unit
        }

    override suspend fun updateInstitution(newInstitution: String) =
        runCatching {
            userDocument().update("institution", newInstitution).await()
            Unit
        }
}