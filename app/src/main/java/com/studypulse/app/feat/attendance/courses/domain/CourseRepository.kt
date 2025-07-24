package com.studypulse.app.feat.attendance.courses.domain

import com.studypulse.app.feat.attendance.courses.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    suspend fun upsertCourse(course: Course): Result<Unit>
    suspend fun getCourseById(id: String): Result<Course?>
    suspend fun getAllCourses(): Result<List<Course>>
    fun getAllCoursesFlow(): Flow<List<Course>>
    fun getAllCoursesSortedByNameFlow(): Result<Flow<List<Course>>>
    suspend fun updateCourse(course: Course): Result<Unit>
    suspend fun deleteCourse(id: String): Result<Unit>
}