package com.studypulse.app.feat.attendance.courses.domain

import com.studypulse.app.feat.attendance.courses.domain.model.Period
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow

interface PeriodRepository {
    suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: String)
    suspend fun addNewPeriod(period: Period)
    suspend fun updateCourseName(periodId: String, newName: String): Result<Unit>
    suspend fun updatePeriod(period: Period): Result<Unit>
    suspend fun getPeriodById(id: String): Result<Period?>
    suspend fun getAllPeriods(): Result<List<Period>>
    /*
        returns the result sorted in ascending order of period start time
     */
    suspend fun getAllPeriodsForCourseByDayInStartTimeOrder(courseId: String, day: DayOfWeek): Result<Flow<List<Period>>>
    suspend fun getAllPeriodsByDayInStartTimeOrder(day: DayOfWeek): Result<Flow<List<Period>>>
    suspend fun deletePeriod(periodId: String): Result<Unit>
}