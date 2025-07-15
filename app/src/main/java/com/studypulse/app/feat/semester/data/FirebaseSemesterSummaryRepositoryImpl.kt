package com.studypulse.app.feat.semester.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import com.studypulse.app.feat.semester.domain.model.SemesterSummary
import com.studypulse.app.feat.semester.domain.model.SemesterSummaryDto
import com.studypulse.app.feat.semester.domain.model.toDomain
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
        .collection("users/${getUserId()}/semesters/summaries")
        .document("summary" + getSemesterId())

    override suspend fun put(): Result<Unit> =
        runCatching {
            summaryDocument().set(SemesterSummaryDto(semesterId = getSemesterId()))
        }

    override suspend fun get(): Result<SemesterSummary> =
        runCatching {
            summaryDocument().get()
                .await()
                .toObject(SemesterSummaryDto::class.java)!!.toDomain()
        }

    override suspend fun incPresent(by: Int): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "presentRecords",
                    FieldValue.increment(by.toLong())
                ).await()
        }

    override suspend fun decPresent(by: Int): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "presentRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }

    override suspend fun incAbsent(by: Int): Result<Unit> =
        runCatching {
            summaryDocument().update(
                "absentRecords",
                FieldValue.increment(by.toLong())
            ).await()
        }

    override suspend fun decAbsent(by: Int): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "absentRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }

    override suspend fun incUnmarked(by: Int): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(by.toLong())
                ).await()
        }

    override suspend fun decUnmarked(by: Int): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }

    override suspend fun incCancelled(by: Int): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "cancelledRecords",
                    FieldValue.increment(by.toLong())
                ).await()
        }

    override suspend fun decCancelled(by: Int): Result<Unit> =
        runCatching {
            summaryDocument()
                .update(
                    "cancelledRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }
}