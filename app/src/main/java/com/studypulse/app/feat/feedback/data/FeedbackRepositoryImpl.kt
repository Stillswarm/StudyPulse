package com.studypulse.app.feat.feedback.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FeedbackRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
) : FeedbackRepository {

    override suspend fun send(message: String) =
        runCatching {
            val uid = auth.currentUser?.uid ?: "anonymous"
            val email = db.collection("users").document(uid).get().await().get("email")?.toString()
            val payload = mapOf(
                "userId" to uid,
                "message" to message,
                "createdAt" to FieldValue.serverTimestamp(),
                "email" to email
            )
            db.collection("feedback")
                .add(payload)
                .addOnFailureListener { e ->
                    Result.failure<Unit>(e)
                }
                .await()
            Unit
        }
}