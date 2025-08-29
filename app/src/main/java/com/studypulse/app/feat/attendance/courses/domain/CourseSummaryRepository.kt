package com.studypulse.app.feat.attendance.courses.domain

import kotlinx.coroutines.flow.Flow

interface CourseSummaryRepository {
    suspend fun put(courseId: String, minAttendance: Int, courseName: String): Result<Unit>
    suspend fun get(courseId: String): Result<CourseSummary>
    suspend fun getFlow(courseId: String): Flow<CourseSummary?>
    suspend fun delete(courseId: String): Result<Unit>
    suspend fun incPresent(courseId: String, by: Long): Result<Unit>
    suspend fun decPresent(courseId: String, by: Long): Result<Unit>
    suspend fun incAbsent(courseId: String, by: Long): Result<Unit>
    suspend fun decAbsent(courseId: String, by: Long): Result<Unit>
    suspend fun incUnmarked(courseId: String, by: Long): Result<Unit>
    suspend fun decUnmarked(courseId: String, by: Long): Result<Unit>
    suspend fun incCancelled(courseId: String, by: Long): Result<Unit>
    suspend fun decCancelled(courseId: String, by: Long): Result<Unit>
    suspend fun getSummaryForAllCourses(): Result<List<CourseSummary>>
    suspend fun getPercentageForAllCourses(): Result<Map<String, Double>>
}