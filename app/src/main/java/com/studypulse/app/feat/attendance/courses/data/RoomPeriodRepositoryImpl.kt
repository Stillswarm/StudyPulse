package com.studypulse.app.feat.attendance.courses.data

import com.studypulse.app.feat.attendance.courses.domain.PeriodDao
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.schedule.data.Day
import com.studypulse.app.feat.attendance.schedule.data.Period
import kotlinx.coroutines.flow.Flow

class RoomPeriodRepositoryImpl(
    private val periodDao: PeriodDao
) : PeriodRepository {
    override suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewPeriod(period: Period) {
        periodDao.addNewPeriod(period)
    }

    override suspend fun getAllPeriodsForCourseFilteredByDayOfWeek(courseId: Long, day: Day): Result<Flow<List<Period>>> {
        return Result.success(periodDao.getAllPeriodsForCourseFilteredByDayOfWeek(courseId, day))
    }

    override suspend fun getAllPeriodsFilteredByDayOfWeek(day: Day): Result<Flow<List<Period>>> {
        return Result.success(periodDao.getAllPeriodsFilteredByDayOfWeek(day))
    }
}