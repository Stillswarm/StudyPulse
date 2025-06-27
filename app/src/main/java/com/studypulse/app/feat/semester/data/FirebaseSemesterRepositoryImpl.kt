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

    override suspend fun addSemester(semester: Semester) =
        runCatching {
            val userId = getUserId()
            db.collection("users").document(userId).collection("semesters").add(semester.toDto())
                .await()
            Unit
        }

    override suspend fun markCurrent(semesterId: String) =
        kotlin.runCatching {
            val userId = getUserId()
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semesterId)
                .update("isCurrent", true)
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
                .whereEqualTo("isCurrent", true)
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