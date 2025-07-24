package com.studypulse.app.feat.attendance.courses.data

import com.studypulse.app.feat.attendance.courses.domain.PeriodDao
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.attendance.courses.domain.model.toPeriod
import com.studypulse.app.feat.attendance.courses.domain.model.toPeriodEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPeriodRepositoryImpl(
    private val periodDao: PeriodDao
) : PeriodRepository {
    override suspend fun getPeriodsByCourseIdSortedByDayOfWeek(courseId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addNewPeriod(period: Period) {
        periodDao.addNewPeriod(period.toPeriodEntity())
    }

    override suspend fun getAllPeriodsForCourseFilteredByDayOfWeek(courseId: String, day: Day): Result<Flow<List<Period>>> {
        return Result.success(periodDao.getAllPeriodsForCourseFilteredByDayOfWeek(courseId.toLong(), day).map { it.map { it.toPeriod() } })
    }

    override suspend fun getAllPeriodsFilteredByDayOfWeek(day: Day): Result<Flow<List<Period>>> {
        return Result.success(periodDao.getAllPeriodsFilteredByDayOfWeek(day))
    }

    override suspend fun deletePeriod(periodId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}