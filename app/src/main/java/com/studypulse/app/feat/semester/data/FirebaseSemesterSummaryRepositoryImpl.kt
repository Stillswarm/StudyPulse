package com.studypulse.app.feat.semester.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import com.studypulse.app.feat.semester.domain.model.SemesterSummary
import com.studypulse.app.feat.semester.domain.model.SemesterSummaryDto
import com.studypulse.app.feat.semester.domain.model.toDomain
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirebaseSemesterSummaryRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val ds: AppDatastore,
) : SemesterSummaryRepository {

    private suspend fun getSemesterId() = ds.semesterIdFlow.first()
    private fun getUserId() =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private suspend fun summaryDocument() = db
        .collection("users/${getUserId()}/semesters/${getSemesterId()}/sem_summaries")
        .document("sem_summary")

    override suspend fun put(minAttendance: Int): Result<Unit> =
        runCatching {
            Log.d("tag", "putting")
            val doc = summaryDocument()
            doc.set(SemesterSummaryDto(semesterId = getSemesterId(), id = doc.id, minAttendance = minAttendance)).await()
            Log.d("tag", "put summary semester")
        }

    override suspend fun get(): Result<SemesterSummary> =
        runCatching {

            val document = summaryDocument().get().await()

            if (!document.exists()) {
                throw Exception("Semester summary document not found")
            }

            val dto = document.toObject(SemesterSummaryDto::class.java)
                ?: throw Exception("Failed to parse semester summary data")

            val domainObject = dto.toDomain()

            domainObject
        }

    override suspend fun incPresent(by: Long): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "presentRecords",
                    FieldValue.increment(by)
                ).await()
        }

    override suspend fun decPresent(by: Long): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "presentRecords",
                    FieldValue.increment(-by)
                ).await()
        }

    override suspend fun incAbsent(by: Long): Result<Unit> =
        runCatching {
            summaryDocument().update(
                "absentRecords",
                FieldValue.increment(by)
            ).await()
        }

    override suspend fun decAbsent(by: Long): Result<Unit> =
        runCatching {
            Log.d("tag", "decrementing absent")
            val doc = summaryDocument()
                .update(
                    "absentRecords",
                    FieldValue.increment(-by)
                )

            Log.d("tag", "obtained doc ref")
            doc.await()
            Log.d("tag", "decremented absent")
            Unit
        }.onFailure { Log.d("tag", "error: $it") }

    override suspend fun incUnmarked(by: Long): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(by)
                ).await()
            Log.d("tag", "incremented unmarked")
        }

    override suspend fun decUnmarked(by: Long): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(-by)
                ).await()
        }

    override suspend fun incCancelled(by: Long): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "cancelledRecords",
                    FieldValue.increment(by)
                ).await()
        }

    override suspend fun decCancelled(by: Long): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "cancelledRecords",
                    FieldValue.increment(-by)
                ).await()
        }
    
    override fun getSummaryFlow() =
        callbackFlow { 
            val listener = summaryDocument()
                .addSnapshotListener { snap, error ->
                    if (error != null) {
                        Log.e("tag", "error: $error")
                    }
                    
                    snap?.let { 
                        val e = it.toObject(SemesterSummaryDto::class.java)?.toDomain()
                        if (e != null) trySend(e)
                    }
                }

            awaitClose { listener.remove() }
        }
}