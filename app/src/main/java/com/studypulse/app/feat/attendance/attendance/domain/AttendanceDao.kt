package com.studypulse.app.feat.attendance.attendance.domain

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecordEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface AttendanceDao {
    @Upsert
    suspend fun upsertAttendance(attendanceRecord: AttendanceRecordEntity)

    @Query("SELECT * FROM AttendanceRecordEntity WHERE date = :date")
    fun getAttendanceByDate(date: LocalDate): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM AttendanceRecordEntity WHERE periodId = :periodId AND date = :date")
    suspend fun getAttendanceForPeriod(periodId: Long, date: LocalDate) : AttendanceRecordEntity?

    @Query("SELECT * FROM AttendanceRecordEntity")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>>
}