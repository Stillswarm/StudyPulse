package com.studypulse.app.feat.attendance.courses.data

import android.util.Log
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
        .collection("users/${getUserId()}/semesters/${getSemesterId()}/courses/$courseId/course_summaries")
        .document("course_summary")

    override suspend fun put(
        courseId: String,
        minAttendance: Int,
        courseName: String,
    ): Result<Unit> =
        runCatching {
            val doc = summaryDocument(courseId)
            doc.set(
                CourseSummaryDto(
                    id = doc.id,
                    courseId = courseId,
                    minAttendance = minAttendance,
                    courseName = courseName
                )
            ).await()
            Log.d("fcuk", "put summary course $courseId")
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

    override suspend fun getSummaryForAllCourses(): Result<List<CourseSummary>> =
        runCatching {
            val userId = getUserId()
            val semesterId = getSemesterId()
            val snapshot = db
                .collectionGroup("course_summaries")
                .whereEqualTo("userId", userId)
                .whereEqualTo("semesterId", semesterId)
                .get()
                .await()

            snapshot.documents
                .mapNotNull { doc ->
                    val dto = doc.toObject(CourseSummaryDto::class.java)
                    dto?.toDomain()
                }
        }

    override suspend fun getPercentageForAllCourses(): Result<Map<String, Double>> =
        runCatching {
            emptyMap()

            // todo: implement
        }
}