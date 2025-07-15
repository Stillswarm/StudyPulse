package com.studypulse.app.feat.attendance.attendance.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecordDto
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.domain.model.toDomain
import com.studypulse.app.feat.attendance.attendance.domain.model.toDto
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class FirebaseAttendanceRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val semesterSummaryRepository: SemesterSummaryRepository,
    private val courseSummaryRepository: CourseSummaryRepository,
) : AttendanceRepository {

    private fun getUserId(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private fun getAttendanceCollection() = db.collection("users")
        .document(getUserId())
        .collection("attendance")

    override suspend fun upsertAttendance(attendanceRecord: AttendanceRecord) {
        val collection = getAttendanceCollection()
        val docId = attendanceRecord.id.ifBlank {
            collection.document().id
        }

        val courseId = attendanceRecord.courseId
        val doc = collection.document(docId).get().await()
        Log.d("tag", "$courseId and ${doc.id}")
        when (doc.get("status")) {
            "PRESENT" -> {
                semesterSummaryRepository.decPresent(1)
                courseSummaryRepository.decPresent(courseId, 1)
            }
            "ABSENT" -> {
                semesterSummaryRepository.decAbsent(1)
                courseSummaryRepository.decAbsent(courseId, 1)
            }
            "UNMARKED" -> {
                Log.d("tag", "inside when unmarked")
                semesterSummaryRepository.decUnmarked(1)
                courseSummaryRepository.decUnmarked(courseId, 1)
            }
            "CANCELLED" -> {
                semesterSummaryRepository.decCancelled(1)
                courseSummaryRepository.decCancelled(courseId, 1)
            }
        }

        val updatedRecord = attendanceRecord.copy(id = docId)
        Log.d("tag", updatedRecord.toString())
        when (updatedRecord.status) {
            AttendanceStatus.PRESENT -> {
                semesterSummaryRepository.incPresent(1)
                courseSummaryRepository.incPresent(courseId, 1)
            }
            AttendanceStatus.ABSENT -> {
                semesterSummaryRepository.incAbsent(1)
                courseSummaryRepository.incAbsent(courseId, 1)
            }
            AttendanceStatus.CANCELLED -> {
                semesterSummaryRepository.incCancelled(1)
                courseSummaryRepository.incCancelled(courseId, 1)
            }
            AttendanceStatus.UNMARKED -> {
                Log.d("tag", "inside when unmarked")
                semesterSummaryRepository.incUnmarked(1)
                courseSummaryRepository.incUnmarked(courseId, 1)
            }
        }
        collection.document(docId).set(updatedRecord.toDto()).await()
    }

    override fun getAttendanceByDate(date: LocalDate): Flow<List<AttendanceRecord>> = callbackFlow {
        val listener = getAttendanceCollection()
            .whereEqualTo("date", date.toString())
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(AttendanceRecordDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(records)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getAttendanceForPeriodAndDate(
        periodId: String,
        date: LocalDate
    ): AttendanceRecord? {
        val dateString = date.toString()
        val snapshot = getAttendanceCollection()
            .whereEqualTo("periodId", periodId)
            .whereEqualTo("date", dateString)
            .limit(1)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.let { doc ->
            try {
                doc.toObject(AttendanceRecordDto::class.java)?.toDomain()
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>> = callbackFlow {
        val listener = getAttendanceCollection()
            .orderBy("date", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(AttendanceRecordDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(records)
            }

        awaitClose { listener.remove() }
    }

    override fun getAttendanceGroupedByCourse(): Flow<Map<String, List<AttendanceRecord>>> =
        callbackFlow {
            val listener = getAttendanceCollection()
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val records = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(AttendanceRecordDto::class.java)?.toDomain()
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    val groupedRecords = records.groupBy { it.courseId }
                    trySend(groupedRecords)
                }

            awaitClose { listener.remove() }
    }


}
