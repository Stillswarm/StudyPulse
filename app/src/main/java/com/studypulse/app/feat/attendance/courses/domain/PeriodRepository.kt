package com.studypulse.app.feat.attendance.courses.domain

import com.studypulse.app.feat.attendance.schedule.data.Day
import com.studypulse.app.feat.attendance.schedule.data.Period
import kotlinx.coroutines.flow.Flow

interface PeriodRepository {
    suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: Long)
    suspend fun addNewPeriod(period: Period)
    suspend fun getAllPeriodsForCourseFilteredByDayOfWeek(courseId: Long, day: Day): Result<Flow<List<Period>>>
    suspend fun getAllPeriodsFilteredByDayOfWeek(day: Day): Result<Flow<List<Period>>>
}