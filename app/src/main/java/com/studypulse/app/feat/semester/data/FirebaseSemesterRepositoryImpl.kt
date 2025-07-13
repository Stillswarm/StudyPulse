package com.studypulse.app.feat.semester.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.model.Semester
import com.studypulse.app.feat.semester.domain.model.SemesterDto
import com.studypulse.app.feat.semester.domain.model.toDomain
import com.studypulse.app.feat.semester.domain.model.toDto
import kotlinx.coroutines.tasks.await

class FirebaseSemesterRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val ds: AppDatastore
) : SemesterRepository {
    private fun getUserId() =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    override suspend fun addActiveSemester(semester: Semester) = runCatching {
        Log.d("fcuk", "eree")
        val userId   = getUserId()
        val now      = System.currentTimeMillis()
        val s        = semester.copy(createdAt = now)
        val col      = db.collection("users")
            .document(userId)
            .collection("semesters")
        val newDoc   = col.document()      // create new docRef

        // 1) fetch any existing “current” semesters
        val snapshot = col
            .whereEqualTo("current", true)
            .get()
            .await()

        // 2) build an atomic batch
        val batch = db.batch()
        snapshot.documents.forEach { doc ->
            batch.update(doc.reference, "current", false)
        }
        batch.set(newDoc, s.toDto().copy(id = newDoc.id, current = true))

        // 3) commit it all in one go
        batch.commit().await()

        // 4) only *then* persist locally
        ds.saveSemesterId(newDoc.id)
    }.onFailure {
        Log.d("fcuk", "$it")
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
