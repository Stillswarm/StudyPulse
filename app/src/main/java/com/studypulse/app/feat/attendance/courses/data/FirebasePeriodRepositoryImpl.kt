package com.studypulse.app.feat.attendance.courses.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.domain.model.toDto
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.attendance.courses.domain.model.PeriodDto
import com.studypulse.app.feat.attendance.courses.domain.model.toDomain
import com.studypulse.app.feat.attendance.courses.domain.model.toDto
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class FirebasePeriodRepositoryImpl(
    private val auth: FirebaseAuth,
    private val semesterRepository: SemesterRepository,
    private val semesterSummaryRepository: SemesterSummaryRepository,
    private val courseSummaryRepository: CourseSummaryRepository,
    private val db: FirebaseFirestore,
    private val ds: AppDatastore,
) : PeriodRepository {
    private suspend fun getActiveSemId() = ds.semesterIdFlow.first()
    override suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewPeriod(period: Period) {
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val periodData = period.copy(
                createdAt = System.currentTimeMillis()
            )
            val semester = semesterRepository.getActiveSemester().getOrNull() ?: return@runCatching Unit

            val ref = db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semester.id)
                .collection("periods")
                .document()
            ref.set(periodData.toDto().copy(id = ref.id)).await()

            var countPast = 0L
            val today = LocalDate.now()
            var current = semester.startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(period.day.name)))
            val batch: WriteBatch = db.batch()
            while (!current.isAfter(semester.endDate)) {
                val attendanceRef = db.collection("users")
                    .document(userId)
                    .collection("attendance")
                    .document()

                val attendanceDto = AttendanceRecord(
                    periodId = ref.id,
                    date = current,
                    id = attendanceRef.id,
                    userId = userId,
                    semesterId = getActiveSemId(),
                    courseId = period.courseId,
                    status = AttendanceStatus.UNMARKED,
                    createdAt = System.currentTimeMillis(),
                    processed = current <= today
                ).toDto()

                if (current <= today) ++countPast

                batch.set(attendanceRef, attendanceDto)
                current = current.plusWeeks(1)
            }

            batch.commit().await()
            semesterSummaryRepository.incUnmarked(countPast)
            courseSummaryRepository.incUnmarked(period.courseId, countPast)
        }
    }

    override suspend fun updatePeriod(period: Period) =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val periodData = period.copy(
                createdAt = System.currentTimeMillis()
            )
            val semester = semesterRepository.getActiveSemester().getOrNull() ?: return@runCatching Unit

            // 1. Query for attendance records related to this period
            val attendanceQuery = db.collection("users")
                .document(userId)
                .collection("attendance")
                .whereEqualTo("periodId", period.id)
                .get()
                .await()

            // 2. Generate correct sequence of dates as per the new period details
            val newDates = mutableListOf<LocalDate>()
            var current = semester.startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(period.day.name)))
            while (!current.isAfter(semester.endDate)) {
                newDates.add(current)
                current = current.plusWeeks(1)
            }

            // 3. Update each attendance record's date field (match only as many as available)
            val batch = db.batch()
            val docs = attendanceQuery.documents
            for (i in docs.indices) {
                val doc = docs[i]
                if (i < newDates.size) {
                    // Only update the 'date' property
                    batch.update(doc.reference, "date", newDates[i].toString())
                }
            }
            batch.commit().await()

            // 4. Update the period document with new data (keeping the same id)
            val periodRef = db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semester.id)
                .collection("periods")
                .document(period.id)
            periodRef.set(periodData.toDto().copy(id = period.id)).await()
            Unit
        }

    override suspend fun getAllPeriodsForCourseFilteredByDayOfWeek(
        courseId: String,
        day: Day
    ) =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            callbackFlow {
                val listener = db.collection("users")
                    .document(userId)
                    .collection("semesters")
                    .document(getActiveSemId())
                    .collection("periods")
                    .whereEqualTo("courseId", courseId)
                    .whereEqualTo("day", day.name)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val periods = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(PeriodDto::class.java)?.copy(id = doc.id)
                        }?.map { it.toDomain() } ?: emptyList()

                        trySend(periods)
                    }

                awaitClose { listener.remove() }
            }
        }

    override suspend fun getAllPeriodsFilteredByDayOfWeek(day: Day) =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            callbackFlow {
                val listener = db.collection("users")
                    .document(userId)
                    .collection("semesters")
                    .document(getActiveSemId())
                    .collection("periods")
                    .whereEqualTo("day", day.name)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) return@addSnapshotListener

                        val periods = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(PeriodDto::class.java)?.copy(id = doc.id)
                        }?.map { it.toDomain() } ?: emptyList()

                        trySend(periods)
                    }

                awaitClose { listener.remove() }
            }
        }

    override suspend fun deletePeriod(periodId: String) =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val semesterId = getActiveSemId()

            // obtain all attendance records associated with this period
            val attendanceQuery = db.collection("users")
                .document(userId)
                .collection("attendance")
                .whereEqualTo("periodId", periodId)
                .get()
                .await()

            // Count the number of each attendance status and courseId
            var presentCount = 0L
            var absentCount = 0L
            var cancelledCount = 0L
            var unmarkedCount = 0L
            var courseId: String? = null
            for (doc in attendanceQuery.documents) {
                val status = doc.getString("status")
                when (status) {
                    "PRESENT" -> presentCount++
                    "ABSENT" -> absentCount++
                    "CANCELLED" -> cancelledCount++
                    "UNMARKED" -> unmarkedCount++
                }
                if (courseId == null) {
                    courseId = doc.getString("courseId")
                }
            }

            // Decrement summary values for this course and semester
            courseId?.let {
                courseSummaryRepository.decPresent(it, presentCount)
                courseSummaryRepository.decAbsent(it, absentCount)
                courseSummaryRepository.decCancelled(it, cancelledCount)
                courseSummaryRepository.decUnmarked(it, unmarkedCount)
            }
            semesterSummaryRepository.decPresent(presentCount)
            semesterSummaryRepository.decAbsent(absentCount)
            semesterSummaryRepository.decCancelled(cancelledCount)
            semesterSummaryRepository.decUnmarked(unmarkedCount)

            val batch = db.batch()
            for (doc in attendanceQuery.documents) {
                batch.delete(doc.reference)
            }

            // obtain the period
            val periodRef = db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semesterId)
                .collection("periods")
                .document(periodId)
            batch.delete(periodRef)

            // batch delete period and all associated records
            batch.commit().await()
            Unit
        }
}