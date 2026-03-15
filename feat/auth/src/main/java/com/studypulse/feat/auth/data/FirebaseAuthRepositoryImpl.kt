package com.studypulse.feat.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.studypulse.core.user.model.User
import com.studypulse.core.user.repository.UserRepository
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
        }

    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> =
        runCatching {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")
            userRepository.addUser(User(email = email, id = uid))
        }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> =
        runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("Google sign-in failed")
            userRepository.addUser(
                User(
                    email = user.email ?: "",
                    id = user.uid,
                    name = user.displayName ?: "",
                )
            )
        }

    /**
     * Sends a password reset email. For security, returns success even if the
     * email is not registered (avoids revealing account existence).
     */
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (_: FirebaseAuthInvalidUserException) {
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Invalid email format"))
        } catch (e: Exception) {
            Result.failure(e)
        }
}
