package com.studypulse.app.feat.attendance.courses.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.attendance.courses.domain.model.PeriodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Insert
    suspend fun addNewPeriod(period: PeriodEntity)

    @Query("SELECT * FROM periodentity WHERE courseId = :courseId AND day = :day")
    fun getAllPeriodsForCourseFilteredByDayOfWeek(courseId: Long, day: Day) : Flow<List<PeriodEntity>>

    @Query("SELECT * FROM periodentity WHERE day = :day")
    fun getAllPeriodsFilteredByDayOfWeek(day: Day): Flow<List<Period>>
}