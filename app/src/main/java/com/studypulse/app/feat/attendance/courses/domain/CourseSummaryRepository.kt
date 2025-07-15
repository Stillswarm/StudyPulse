package com.studypulse.app.feat.attendance.courses.domain

interface CourseSummaryRepository {
    suspend fun put(courseId: String, minAttendance: Int, courseName: String): Result<Unit>
    suspend fun get(courseId: String): Result<CourseSummary>
    suspend fun incPresent(courseId: String, by: Int): Result<Unit>
    suspend fun decPresent(courseId: String, by: Int): Result<Unit>
    suspend fun incAbsent(courseId: String, by: Int): Result<Unit>
    suspend fun decAbsent(courseId: String, by: Int): Result<Unit>
    suspend fun incUnmarked(courseId: String, by: Int): Result<Unit>
    suspend fun decUnmarked(courseId: String, by: Int): Result<Unit>
    suspend fun incCancelled(courseId: String, by: Int): Result<Unit>
    suspend fun decCancelled(courseId: String, by: Int): Result<Unit>
    suspend fun getSummaryForAllCourses(): Result<List<CourseSummary>>
    suspend fun getPercentageForAllCourses(): Result<Map<String, Double>>
}