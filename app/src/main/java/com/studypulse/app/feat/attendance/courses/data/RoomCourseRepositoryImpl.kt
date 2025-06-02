package com.studypulse.app.feat.attendance.courses.data

import com.studypulse.app.feat.attendance.courses.domain.CourseDao
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import kotlinx.coroutines.flow.Flow

class RoomCourseRepositoryImpl(
    private val courseDao: CourseDao
) : CourseRepository {
    override fun getAllCourses(): Result<Flow<List<Course>>> {
        return Result.success(courseDao.getAllCourses())
    }

    override fun getAllCoursesSortedByName(): Result<Flow<List<Course>>> {
        return Result.success(courseDao.getAllCoursesSortedByName())
    }

    override suspend fun getCourseById(id: Long): Result<Course?> {
        return Result.success(courseDao.getCourseById(id))
    }

    override suspend fun addCourse(course: Course): Result<Unit> {
        return Result.success(courseDao.insertCourse(course))
    }

    override suspend fun updateCourse(course: Course): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCourse(id: Int): Result<Unit> {
        TODO("Not yet implemented")
    }
}