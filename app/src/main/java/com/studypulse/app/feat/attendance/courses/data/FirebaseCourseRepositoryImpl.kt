package com.studypulse.app.feat.attendance.courses.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.feat.attendance.courses.domain.model.CourseDto
import com.studypulse.app.feat.attendance.courses.domain.model.toDomain
import com.studypulse.app.feat.attendance.courses.domain.model.toDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

class FirebaseCourseRepositoryImpl(
    private val db: FirebaseFirestore,
    private val ds: AppDatastore,
    private val auth: FirebaseAuth
) : CourseRepository {
    suspend fun getSemesterId(): String = ds.semesterIdFlow.first()
    override fun getAllCourses(): Result<Flow<List<Course>>> =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            callbackFlow {
                val listener = db.collection("users")
                    .document(userId)
                    .collection("semesters")
                    .document(getSemesterId())
                    .collection("courses")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val courses = snapshot?.documents
                            ?.mapNotNull { doc ->
                                doc.toObject(CourseDto::class.java)?.copy(id = doc.id)
                            }?.map {
                                it.toDomain()
                            } ?: emptyList()

                        trySend(courses)
                    }

                awaitClose { listener.remove() }
            }
        }

    override fun getAllCoursesSortedByName(): Result<Flow<List<Course>>> =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            callbackFlow {
                val listener = db.collection("users")
                    .document(userId)
                    .collection("semesters")
                    .document(getSemesterId())
                    .collection("courses")
                    .orderBy("courseName")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val courses = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(CourseDto::class.java)?.copy(id = doc.id)
                        }?.map { it.toDomain() } ?: emptyList()

                        trySend(courses)
                    }

                awaitClose { listener.remove() }
            }
        }

    override suspend fun getCourseById(id: String): Result<Course?> =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val doc = db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(getSemesterId())
                .collection("courses")
                .document(id)
                .get()
                .await()

            doc.toObject(CourseDto::class.java)?.copy(id = doc.id)?.toDomain()
        }

    override suspend fun addCourse(course: Course): Result<Unit> =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val courseData = course.copy(
                createdAt = System.currentTimeMillis()
            )
            val semesterId = ds.semesterIdFlow.first()

            val docRef = if (course.id.isBlank()) {
                db.collection("users")
                    .document(userId)
                    .collection("semesters")
                    .document(semesterId)
                    .collection("courses")
                    .document()            // generates new random ID
            } else {
                db.collection("users")
                    .document(userId)
                    .collection("semesters")
                    .document(semesterId)
                    .collection("courses")
                    .document(course.id)
            }

            docRef.set(courseData.toDto().copy(id = docRef.id))
                .await()
        }

    override suspend fun updateCourse(course: Course): Result<Unit> =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val courseId = course.id
            val semesterId = ds.semesterIdFlow.first()

            db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semesterId)
                .collection("courses")
                .document(courseId)
                .set(course.toDto())
                .await()
        }

    override suspend fun deleteCourse(id: String): Result<Unit> =
        runCatching {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            val semesterId = ds.semesterIdFlow.first()

            db.collection("users")
                .document(userId)
                .collection("semesters")
                .document(semesterId)
                .collection("courses")
                .document(id)
                .delete()
                .await()
        }
}