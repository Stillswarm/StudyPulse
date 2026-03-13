package com.studypulse.app.feat.attendance.courses.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.attendance.courses.domain.model.PeriodEntity
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow

@Dao
interface PeriodDao {
    @Insert
    suspend fun addNewPeriod(period: PeriodEntity)

    @Query("SELECT * FROM periodentity WHERE courseId = :courseId AND day = :day")
    fun getAllPeriodsForCourseFilteredByDayOfWeek(courseId: Long, day: DayOfWeek): Flow<List<PeriodEntity>>

    @Query("SELECT * FROM periodentity WHERE day = :day")
    fun getAllPeriodsFilteredByDayOfWeek(day: DayOfWeek): Flow<List<Period>>
}