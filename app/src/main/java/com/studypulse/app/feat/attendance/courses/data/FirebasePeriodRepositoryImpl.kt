package com.studypulse.app.feat.attendance.courses.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.domain.model.toDto
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.attendance.courses.domain.model.PeriodDto
import com.studypulse.app.feat.attendance.courses.domain.model.toDomain
import com.studypulse.app.feat.attendance.courses.domain.model.toDto
import com.studypulse.app.feat.semester.domain.SemesterRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

class FirebasePeriodRepositoryImpl(
    private val auth: FirebaseAuth,
    private val semesterRepository: SemesterRepository,
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

            var current = semester.startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(period.day.name)))
            val batch: WriteBatch = db.batch()
            while (!current.isAfter(semester.endDate)) {
                val attendanceRef = db.collection("users")
                    .document(userId)
                    .collection("semesters")
                    .document(semester.id)
                    .collection("periods")
                    .document(ref.id)
                    .collection("attendance")
                    .document()

                val attendanceDto = AttendanceRecord(
                    periodId = ref.id,
                    date = current,
                    id = attendanceRef.id,
                    courseId = period.courseId,
                    status = AttendanceStatus.UNMARKED,
                    createdAt = System.currentTimeMillis(),
                ).toDto()

                batch.set(attendanceRef, attendanceDto)
                current = current.plusWeeks(1)
            }

            batch.commit().await()
        }
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
}