package com.studypulse.app.feat.attendance.attendance.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.studypulse.app.common.util.toTimestamp
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecordDto
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.domain.model.toDomain
import com.studypulse.app.feat.attendance.attendance.domain.model.toDto
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate

class FirebaseAttendanceRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val semesterSummaryRepository: SemesterSummaryRepository,
    private val courseSummaryRepository: CourseSummaryRepository,
) : AttendanceRepository {

    private fun getUserId(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private fun getAttendanceCollection() =
        db.collection("users/${getUserId()}/attendance")

    override suspend fun upsertAttendance(attendanceRecord: AttendanceRecord) {
        val collection = getAttendanceCollection()
        val docId = attendanceRecord.id.ifBlank {
            collection.document().id
        }
        Log.d("tag", getUserId())

        val courseId = attendanceRecord.courseId
        val doc = collection.document(docId).get().await()
        Log.d("tag", "about  to unmark")
        when (doc.get("status")) {
            "PRESENT" -> {
                semesterSummaryRepository.decPresent(1)
                courseSummaryRepository.decPresent(courseId, 1)
            }

            "ABSENT" -> {
                Log.d("tag", "about  to unmark 1")
                semesterSummaryRepository.decAbsent(1)
                Log.d("tag", "about  to unmark 2")
                courseSummaryRepository.decAbsent(courseId, 1)
                Log.d("tag", "about  to unmark 3")
            }

            "UNMARKED" -> {
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
                Log.d("tag", "p")
                semesterSummaryRepository.incPresent(1)
                courseSummaryRepository.incPresent(courseId, 1)
            }

            AttendanceStatus.ABSENT -> {
                Log.d("tag", "a")
                semesterSummaryRepository.incAbsent(1)
                courseSummaryRepository.incAbsent(courseId, 1)
            }

            AttendanceStatus.CANCELLED -> {
                Log.d("tag", "c")
                semesterSummaryRepository.incCancelled(1)
                courseSummaryRepository.incCancelled(courseId, 1)
            }

            AttendanceStatus.UNMARKED -> {
                Log.d("tag", "u")
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
        date: LocalDate,
    ): AttendanceRecord? {
        Log.d("tag", "inside getAttendanceForPeriodAndDate")
        val dateTimestamp = date.toTimestamp()
        Log.d("tag", "dateTimestamp: $dateTimestamp")
        val snapshot = db.collectionGroup("attendance")
            .whereEqualTo("periodId", periodId)
            .whereEqualTo("date", dateTimestamp)
            .limit(1)
            .get()
            .await()

        Log.d("tag", "size: ${snapshot.documents.size}")
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

    override suspend fun getDatesWithUnmarkedAttendance(
        semesterId: String,
        monthStartDate: LocalDate,
        endDate: LocalDate
    ): Result<Set<LocalDate>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = getAttendanceCollection()
                .whereEqualTo("semesterId", semesterId)
                .whereGreaterThanOrEqualTo("date", monthStartDate.toTimestamp())
                .whereLessThanOrEqualTo("date", endDate.toTimestamp())
                .whereEqualTo("status", AttendanceStatus.UNMARKED.name)
                .get()
                .await()

            val dates = snapshot.documents.mapNotNull { doc ->
                try {
                    val timestamp = doc.getTimestamp("date")
                    timestamp?.toDate()?.toInstant()?.atZone(java.time.ZoneId.systemDefault())?.toLocalDate()
                } catch (e: Exception) {

                    null
                }
            }.toSet()
            Result.success(dates)
        } catch (e: Exception) {
            Log.e("AttendanceRepo", "Error fetching dates with pending attendance", e)
            Result.failure(e)
        }
    }

    override suspend fun hasPendingAttendanceForDate(semesterId: String, date: LocalDate): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val snapshot = getAttendanceCollection()
                .whereEqualTo("semesterId", semesterId)
                .whereEqualTo("date", date.toTimestamp())
                .whereEqualTo("status", AttendanceStatus.UNMARKED.name)
                .limit(1)
                .get()
                .await()
            Result.success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Log.e("AttendanceRepo", "Error checking pending attendance for date $date", e)
            Result.failure(e)
        }
    }

    override suspend fun upsertManyAttendance(records: List<AttendanceRecord>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val batch = db.batch()
            val collection = getAttendanceCollection()
            records.forEach { record ->
                val docId = record.id.ifBlank { collection.document().id }
                val updatedRecord = record.copy(id = docId)
                val docRef = collection.document(docId)
                batch.set(docRef, updatedRecord.toDto())
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AttendanceRepo", "Error bulk upserting attendance", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAttendance(attendanceRecordId: String) =
        runCatching {
            getAttendanceCollection().document(attendanceRecordId).delete().await()
            Unit
        }
}
