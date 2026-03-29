package com.studypulse.feat.attendance.courses.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.core.firebase.BaseFirebaseRepository
import com.studypulse.core.semester.datastore.AppDatastore
import com.studypulse.feat.attendance.courses.domain.CourseRepository
import com.studypulse.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.feat.attendance.courses.domain.model.Course
import com.studypulse.feat.attendance.courses.domain.model.CourseDto
import com.studypulse.feat.attendance.courses.domain.model.toDomain
import com.studypulse.feat.attendance.courses.domain.model.toDto
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseCourseRepositoryImpl(
    db: FirebaseFirestore,
    private val ds: AppDatastore,
    auth: FirebaseAuth,
    private val courseSummaryRepository: CourseSummaryRepository,
    private val periodRepository: PeriodRepository,
) : BaseFirebaseRepository(auth, db), CourseRepository {
    suspend fun getSemesterId(): String = ds.semesterIdFlow.first()
    override fun getAllCoursesFlow(): Flow<List<Course>> {
        return callbackFlow {
            val listener = userCollection("semesters", getSemesterId(), "courses")
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

    override fun getAllCoursesSortedByNameFlow(): Result<Flow<List<Course>>> =
        runCatching {
            callbackFlow {
                val listener = userCollection("semesters", getSemesterId(), "courses")
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

    override suspend fun getAllCourses() = runCatching {
        val snapshot = userCollection("semesters", getSemesterId(), "courses")
            .get()
            .await()

        snapshot.documents.mapNotNull { it.toObject(CourseDto::class.java)?.toDomain() }

    }

    override suspend fun getCourseById(id: String): Result<Course?> =
        runCatching {
            val doc = userCollection("semesters", getSemesterId(), "courses")
                .document(id)
                .get()
                .await()

            doc.toObject(CourseDto::class.java)?.copy(id = doc.id)?.toDomain()
        }

    override suspend fun upsertCourse(course: Course): Result<Unit> =
        runCatching {
            if (course.id.isNotEmpty()) {
                updateCourse(course)
            }
            val courseData = course.copy(
                createdAt = System.currentTimeMillis()
            )
            val semesterId = ds.semesterIdFlow.first()
            val coursesCol = userCollection("semesters", semesterId, "courses")

            val docRef = if (course.id.isBlank()) {
                coursesCol.document()
            } else {
                coursesCol.document(course.id)
            }

            docRef.set(courseData.toDto().copy(id = docRef.id))
                .await()
            courseSummaryRepository.put(docRef.id, course.minAttendance, course.courseName)
        }

    override suspend fun updateCourse(course: Course): Result<Unit> =
        runCatching {
            val courseId = course.id
            val semesterId = ds.semesterIdFlow.first()

            coroutineScope {
                val putCourseSummaryJob = launch {
                    courseSummaryRepository.put(courseId, course.minAttendance, course.courseName).getOrThrow()
                }
                val periodsDeferred = async {
                    val periodQuery = userCollection("semesters", semesterId, "periods")
                        .whereEqualTo("courseId", courseId)
                        .get()
                        .await()
                    periodQuery.documents
                }
                val updatePeriodsJob = launch {
                    val periods = periodsDeferred.await()
                    for (periodDoc in periods) {
                        periodRepository.updateCourseName(periodDoc.id, course.courseName).getOrThrow()
                    }
                }
                val updateCourseJob = launch {
                    userCollection("semesters", semesterId, "courses")
                        .document(courseId)
                        .set(course.toDto())
                        .await()
                }
                // await all
                putCourseSummaryJob.join()
                updatePeriodsJob.join()
                updateCourseJob.join()
            }
        }

    override suspend fun deleteCourse(id: String): Result<Unit> =
        runCatching {
            val semesterId = ds.semesterIdFlow.first()

            val periodQuery = userCollection("semesters", semesterId, "periods")
                .whereEqualTo("courseId", id)
                .get()
                .await()

            coroutineScope {
                val deleteJobs = periodQuery.documents.map { periodDoc ->
                    async {
                        periodRepository.deletePeriod(periodDoc.id)
                    }
                }
                deleteJobs.awaitAll()

                launch { courseSummaryRepository.delete(id) }

                launch {
                    userCollection("semesters", semesterId, "courses")
                        .document(id)
                        .delete()
                        .await()
                }
            }
        }
}