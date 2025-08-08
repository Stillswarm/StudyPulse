package com.studypulse.app.feat.attendance.courses.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.common.util.toTimestamp
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
import com.studypulse.app.feat.attendance.notification.AlarmScheduler
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    private val context: Context,
) : PeriodRepository {
    private suspend fun getActiveSemId() = ds.semesterIdFlow.first()
    override suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewPeriod(period: Period) {
        runCatching {
            if (period.id.isNotEmpty()) {
                updatePeriod(period)
                return@runCatching
            }
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

            val newPeriod = period.copy(id = ref.id)    // to send through to notification handler

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

            // register for alarm notifications
            Log.d("AttendanceActionReceiver", "period id from fprimpl: ${newPeriod.id}")
            AlarmScheduler.scheduleAlarmForPeriod(context, newPeriod, userId)
        }
    }

    override suspend fun updateCourseName(
        periodId: String,
        newName: String,
    ) = runCatching { 
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        val semester = semesterRepository.getActiveSemester().getOrNull() ?: return@runCatching Unit

        val periodRef = db.collection("users")
            .document(userId)
            .collection("semesters")
            .document(semester.id)
            .collection("periods")
            .document(periodId)
        
        periodRef.update("courseName", newName).await()
        Unit
    }

    override suspend fun updatePeriod(period: Period) =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val semester =
                semesterRepository.getActiveSemester().getOrNull() ?: return@runCatching Unit

            val periodRef = db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semester.id)
                .collection("periods")
                .document(period.id)

            Log.d("tag", "period ref obtained ")

            val currentDay = periodRef.get().await().get("day")
            if (currentDay != null && currentDay != period.day.name) {
                // Query for attendance records related to this period
                val attendanceDocs = db.collection("users")
                    .document(userId)
                    .collection("attendance")
                    .whereEqualTo("periodId", period.id)
                    .orderBy("date")
                    .get()
                    .await()
                    .documents
                val batch = db.batch()


                var current =
                    semester.startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(period.day.name)))
                var idx = 0
                while (!current.isAfter(semester.endDate) && idx < attendanceDocs.size) {
                    batch.update(attendanceDocs[idx].reference, "date", current.toTimestamp())
                    ++idx
                    current = current.plusWeeks(1)
                }
                batch.commit().await()
            }

            // cancel already existing alarm for previous instance of this period, and schedule a new one
            AlarmScheduler.cancelAlarm(context, period.id)
            AlarmScheduler.scheduleAlarmForPeriod(context, period, userId)

            periodRef.set(period.toDto()).await()
            Unit
        }

    override suspend fun getPeriodById(id: String) =
        runCatching {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(getActiveSemId())
                .collection("periods")
                .document(id)
                .get()
                .await()
                .toObject(PeriodDto::class.java)
                ?.toDomain()
        }

    override suspend fun getAllPeriods() =
        runCatching {
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(getActiveSemId())
                .collection("periods")
                .get()
                .await()
                .toObjects(PeriodDto::class.java)
                .map { it.toDomain() }
        }

    override suspend fun getAllPeriodsForCourseByDayInStartTimeOrder(
        courseId: String,
        day: Day,
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
                    .orderBy("startTime", Query.Direction.ASCENDING)
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

    override suspend fun getAllPeriodsByDayInStartTimeOrder(day: Day) =
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
                    .orderBy("startTime", Query.Direction.ASCENDING)
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
                val processed = doc.getBoolean("processed")
                if (processed ?: false) {
                    when (status) {
                        "PRESENT" -> presentCount++
                        "ABSENT" -> absentCount++
                        "CANCELLED" -> cancelledCount++
                        "UNMARKED" -> unmarkedCount++
                    }
                }
                if (courseId == null) {
                    courseId = doc.getString("courseId")
                }
            }

            Log.d("tag", "presentCount: $presentCount, absentCount: $absentCount, cancelledCount: $cancelledCount, unmarkedCount: $unmarkedCount")

            // Decrement summary values for this course and semester
            coroutineScope {
                courseId?.let {
                    launch { courseSummaryRepository.decPresent(it, presentCount) }
                    launch { courseSummaryRepository.decAbsent(it, absentCount) }
                    launch { courseSummaryRepository.decCancelled(it, cancelledCount) }
                    launch { courseSummaryRepository.decUnmarked(it, unmarkedCount) }
                }
                launch { semesterSummaryRepository.decPresent(presentCount) }
                launch { semesterSummaryRepository.decAbsent(absentCount) }
                launch { semesterSummaryRepository.decCancelled(cancelledCount) }
                launch { semesterSummaryRepository.decUnmarked(unmarkedCount) }
            }

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

            // deregister from alarm notifications
            AlarmScheduler.cancelAlarm(context, periodId)
        }
}