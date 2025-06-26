package com.studypulse.app.feat.attendance.courses.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.attendance.courses.domain.model.PeriodDto
import com.studypulse.app.feat.attendance.courses.domain.model.toDomain
import com.studypulse.app.feat.attendance.courses.domain.model.toDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebasePeriodRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : PeriodRepository {
    override suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewPeriod(period: Period) {
        runCatching { 
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val periodData = period.copy(
                createdAt = System.currentTimeMillis()
            )
            
            db.collection("users")
                .document(userId)
                .collection("periods")
                .document()
                .set(periodData.toDto())
                .await()
        }
    }

    override suspend fun getAllPeriodsForCourseFilteredByDayOfWeek(
        courseId: String,
        day: Day
    ) =
        runCatching { 
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            callbackFlow { 
                val listener = db.collection("users")
                    .document(userId)
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
            val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            callbackFlow {
                val listener = db.collection("users")
                    .document(userId)
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