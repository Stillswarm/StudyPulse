package com.studypulse.app.feat.attendance.courses.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.attendance.courses.domain.model.CourseSummaryDto
import com.studypulse.app.feat.attendance.courses.domain.model.toDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirebaseCourseSummaryRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val ds: AppDatastore,
) : CourseSummaryRepository {

    private suspend fun getSemesterId() = ds.semesterIdFlow.first()
    private fun getUserId() =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    private suspend fun summaryDocument(courseId: String) = db
        .collection("users/${getUserId()}/semesters/${getSemesterId()}/courses/summaries")
        .document("summary$courseId")

    override suspend fun put(courseId: String): Result<Unit> =
        runCatching {
            summaryDocument(courseId).set(CourseSummaryDto(courseId = courseId))
        }

    override suspend fun get(courseId: String): Result<CourseSummary> =
        runCatching {
            summaryDocument(courseId).get().await()
                .toObject(CourseSummaryDto::class.java)!!.toDomain()
        }

    override suspend fun incPresent(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "presentRecords",
                    FieldValue.increment(by.toLong())
                ).await()
        }

    override suspend fun decPresent(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "presentRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }

    override suspend fun incAbsent(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId).update(
                "absentRecords",
                FieldValue.increment(by.toLong())
            ).await()
        }

    override suspend fun decAbsent(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "absentRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }

    override suspend fun incUnmarked(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(by.toLong())
                ).await()
        }

    override suspend fun decUnmarked(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }

    override suspend fun incCancelled(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "cancelledRecords",
                    FieldValue.increment(by.toLong())
                ).await()
        }

    override suspend fun decCancelled(courseId: String, by: Int): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "cancelledRecords",
                    FieldValue.increment(by.toLong().inv())
                ).await()
        }
}