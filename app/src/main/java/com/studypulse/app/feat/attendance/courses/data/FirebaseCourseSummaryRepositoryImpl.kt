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
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseCourseSummaryRepositoryImpl(
    private val auth: FirebaseAuth,
    private val semesterSummaryRepository: SemesterSummaryRepository,
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
            val existingRecord = doc.get().await().toObject(CourseSummaryDto::class.java)
            doc.set(
                CourseSummaryDto(
                    id = doc.id,
                    courseId = courseId,
                    minAttendance = minAttendance,
                    courseName = courseName,
                    userId = getUserId(),
                    semesterId = getSemesterId(),
                    presentRecords = existingRecord?.presentRecords ?: 0,
                    absentRecords = existingRecord?.absentRecords ?: 0,
                    unmarkedRecords = existingRecord?.unmarkedRecords ?: 0,
                    cancelledRecords = existingRecord?.cancelledRecords ?: 0,
                )
            ).await()
        }



    override suspend fun get(courseId: String): Result<CourseSummary> =
        runCatching {
            summaryDocument(courseId).get().await()
                .toObject(CourseSummaryDto::class.java)!!.toDomain()
        }

    override suspend fun getFlow(courseId: String): Flow<CourseSummary?> =
        callbackFlow {
            summaryDocument(courseId).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                }
                val summary = snapshot?.toObject(CourseSummaryDto::class.java)?.toDomain()
                trySend(summary)
            }
        }

    override suspend fun delete(courseId: String): Result<Unit> = runCatching {
        val docSnapshot = summaryDocument(courseId).get().await()

        if (!docSnapshot.exists()) {
            Log.w(
                "FirebaseCourseSummaryRepo",
                "Course summary document $courseId does not exist. Nothing to delete or update from."
            )
            return@runCatching // Successfully did nothing
        }

        // Safely get values, defaulting to 0L. Consider if 0L is the correct default.
        val absent = docSnapshot.getLong("absentRecords") ?: 0L
        val present = docSnapshot.getLong("presentRecords") ?: 0L
        val unmarked = docSnapshot.getLong("unmarkedRecords") ?: 0L
        val cancelled = docSnapshot.getLong("cancelledRecords") ?: 0L

        coroutineScope {
            // reset this course summary's data in the semester summary
            launch { semesterSummaryRepository.decPresent(present) }
            launch { semesterSummaryRepository.decAbsent(absent) }
            launch { semesterSummaryRepository.decCancelled(cancelled) }
            launch { semesterSummaryRepository.decUnmarked(unmarked) }

            // now delete the document itself
            launch { summaryDocument(courseId).delete().await() }
        }
        Log.d(
            "tag",
            "Attempted deletion and semester summary update for $courseId."
        )
    }.onFailure { exception ->
        Log.e(
            "tag",
            "Error during delete operation for course $courseId: ${exception.message}",
            exception
        )
    }

    override suspend fun incPresent(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "presentRecords",
                    FieldValue.increment(by)
                ).await()
        }

    override suspend fun decPresent(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "presentRecords",
                    FieldValue.increment(-by)
                ).await()
        }

    override suspend fun incAbsent(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId).update(
                "absentRecords",
                FieldValue.increment(by)
            ).await()
        }

    override suspend fun decAbsent(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "absentRecords",
                    FieldValue.increment(-by)
                ).await()
        }

    override suspend fun incUnmarked(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(by)
                ).await()
        }

    override suspend fun decUnmarked(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "unmarkedRecords",
                    FieldValue.increment(-by)
                ).await()
        }

    override suspend fun incCancelled(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "cancelledRecords",
                    FieldValue.increment(by)
                ).await()
        }

    override suspend fun decCancelled(courseId: String, by: Long): Result<Unit> =
        runCatching {
            summaryDocument(courseId)
                .update(
                    "cancelledRecords",
                    FieldValue.increment(-by)
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

            Log.d("tag", "sz: ${snapshot.documents.size}")

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