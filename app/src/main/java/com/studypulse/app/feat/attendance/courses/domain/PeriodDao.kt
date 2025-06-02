package com.studypulse.app.feat.attendance.courses.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studypulse.app.feat.attendance.schedule.data.Day
import com.studypulse.app.feat.attendance.schedule.data.Period
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Insert
    suspend fun addNewPeriod(period: Period)

    @Query("SELECT * FROM Period WHERE courseId = :courseId AND day = :day")
    fun getAllPeriodsForCourseFilteredByDayOfWeek(courseId: Long, day: Day) : Flow<List<Period>>

    @Query("SELECT * FROM Period WHERE day = :day")
    fun getAllPeriodsFilteredByDayOfWeek(day: Day): Flow<List<Period>>
}