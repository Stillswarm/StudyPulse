package com.studypulse.app.feat.attendance.courses.domain

import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import kotlinx.coroutines.flow.Flow

interface PeriodRepository {
    suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: String)
    suspend fun addNewPeriod(period: Period)
    suspend fun getAllPeriodsForCourseFilteredByDayOfWeek(courseId: String, day: Day): Result<Flow<List<Period>>>
    suspend fun getAllPeriodsFilteredByDayOfWeek(day: Day): Result<Flow<List<Period>>>
    suspend fun deletePeriod(periodId: String): Result<Unit>
}