package com.studypulse.app.feat.attendance.attendance.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecordDto
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.domain.model.toDomain
import com.studypulse.app.feat.attendance.attendance.domain.model.toDto
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
) : AttendanceRepository {

    private fun getUserId(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

    private fun getAttendanceCollection() = db.collection("users")
        .document(getUserId())
        .collection("attendance")

    override suspend fun upsertAttendance(attendanceRecord: AttendanceRecord) {
        val collection = getAttendanceCollection()
        val docId = attendanceRecord.id.ifBlank {
            collection.document().id        // should never happen as all records are added when period is created
        }

        val doc = collection.document(docId).get().await()
        when (doc.get("status")) {
            "PRESENT" -> semesterSummaryRepository.decPresent(1)
            "ABSENT" ->semesterSummaryRepository.decAbsent(1)
            "UNMARKED" -> semesterSummaryRepository.decUnmarked(1)
            "CANCELLED" -> semesterSummaryRepository.decCancelled(1)
        }

        val updatedRecord = attendanceRecord.copy(id = docId)
        when (updatedRecord.status) {
            AttendanceStatus.PRESENT -> semesterSummaryRepository.incPresent(1)
            AttendanceStatus.ABSENT -> semesterSummaryRepository.decAbsent(1)
            AttendanceStatus.CANCELLED -> semesterSummaryRepository.decCancelled(1)
            AttendanceStatus.UNMARKED -> semesterSummaryRepository.decUnmarked(1)
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
