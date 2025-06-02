package com.studypulse.app.feat.attendance.courses.domain

import com.studypulse.app.feat.attendance.courses.data.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getAllCourses(): Result<Flow<List<Course>>>
    fun getAllCoursesSortedByName(): Result<Flow<List<Course>>>
    suspend fun getCourseById(id: Long): Result<Course?>
    suspend fun addCourse(course: Course): Result<Unit>
    suspend fun updateCourse(course: Course): Result<Unit>
    suspend fun deleteCourse(id: Int): Result<Unit>
}