package com.studypulse.app.feat.semester.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.model.Semester
import com.studypulse.app.feat.semester.domain.model.SemesterDto
import com.studypulse.app.feat.semester.domain.model.toDomain
import com.studypulse.app.feat.semester.domain.model.toDto
import kotlinx.coroutines.tasks.await

class FirebaseSemesterRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : SemesterRepository {
    private fun getUserId() =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    override suspend fun addActiveSemester(semester: Semester) =
        runCatching {
            val userId = getUserId()
            val s = semester.copy(createdAt = System.currentTimeMillis())
            // First, set current = false for all existing semesters
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .whereEqualTo("current", true)
                .get()
                .await()
                .documents
                .forEach { document ->
                    document.reference.update("current", false).await()
                }
            // Then, add the new semester with current = true
            val docRef =
                db.collection("users").document(userId).collection("semesters").add(s.toDto())
                    .await()
            // Update the document with its own ID
            docRef.update("id", docRef.id).await()
            Unit
        }

    override suspend fun markCurrent(semesterId: String) =
        kotlin.runCatching {
            val userId = getUserId()
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .whereEqualTo("current", true)
                .get()
                .await()
                .documents
                .forEach { document ->
                    document.reference.update("current", false).await()
                }

            db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semesterId)
                .update("current", true)
            Unit
        }

    override suspend fun markCompleted(semesterId: String) =
        runCatching {
            val userId = getUserId()
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semesterId)
                .update("isCompleted", false)
            Unit
        }

    override suspend fun getActiveSemester() =
        runCatching {
            val userId = getUserId()
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .whereEqualTo("current", true)
                .get()
                .await()
                .toObjects(SemesterDto::class.java)
                .map { it.toDomain() }
                .firstOrNull()
        }

    override suspend fun getAllSemesters() =
        runCatching {
            val userId = getUserId()
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .get()
                .await()
                .toObjects(SemesterDto::class.java)
                .map { it.toDomain() }
        }
}
