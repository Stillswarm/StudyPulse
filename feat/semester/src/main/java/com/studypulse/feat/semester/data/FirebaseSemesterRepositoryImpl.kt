package com.studypulse.feat.semester.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.core.semester.datastore.AppDatastore
import com.studypulse.core.semester.model.Semester
import com.studypulse.core.semester.repository.SemesterRepository
import com.studypulse.core.semester.repository.SemesterSummaryRepository
import kotlinx.coroutines.tasks.await

class FirebaseSemesterRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val ds: AppDatastore,
    private val semesterSummaryRepository: SemesterSummaryRepository
) : SemesterRepository {
    private fun getUserId() =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    override suspend fun addActiveSemester(semester: Semester) = runCatching {
        val userId   = getUserId()
        val now      = System.currentTimeMillis()
        val s        = semester.copy(createdAt = now)
        val col      = db.collection("users")
            .document(userId)
            .collection("semesters")
        val newDoc   = col.document()

        val snapshot = col
            .whereEqualTo("current", true)
            .get()
            .await()

        val batch = db.batch()
        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "current", false)
        }
        batch.set(newDoc, s.toDto().copy(id = newDoc.id, current = true))

        batch.commit().await()

        ds.saveSemesterId(newDoc.id)

        semesterSummaryRepository.put(minAttendance = semester.minAttendance)
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

            ds.saveSemesterId(semesterId)
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
