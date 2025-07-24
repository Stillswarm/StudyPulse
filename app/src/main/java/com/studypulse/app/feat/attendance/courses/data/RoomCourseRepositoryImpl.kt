package com.studypulse.app.feat.attendance.courses.data

import com.studypulse.app.feat.attendance.courses.domain.CourseDao
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.feat.attendance.courses.domain.model.toDomain
import com.studypulse.app.feat.attendance.courses.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomCourseRepositoryImpl(
    private val courseDao: CourseDao
) : CourseRepository {
    override fun getAllCoursesFlow(): Flow<List<Course>> {
        return courseDao.getAllCourses().map { it.map { course -> course.toDomain() } }
    }

    override fun getAllCoursesSortedByNameFlow(): Result<Flow<List<Course>>> {
        return Result.success(courseDao.getAllCoursesSortedByName().map { it.map { c -> c.toDomain() } })
    }

    override suspend fun getCourseById(id: String): Result<Course?> {
        return Result.success(courseDao.getCourseById(id.toLongOrNull() ?: 0L).toDomain())
    }

    override suspend fun upsertCourse(course: Course): Result<Unit> {
        return Result.success(courseDao.insertCourse(course.toEntity()))
    }

    override suspend fun updateCourse(course: Course): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCourse(id: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCourses(): Result<List<Course>> {
        TODO("Not yet implemented")
    }
}