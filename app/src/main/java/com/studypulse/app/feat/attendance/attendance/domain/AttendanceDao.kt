package com.studypulse.app.feat.attendance.attendance.domain

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Upsert
    suspend fun upsertAttendance(attendanceRecord: AttendanceRecord)

    @Query("SELECT * FROM AttendanceRecord WHERE date = :date")
    fun getAttendanceByDate(date: LocalDate): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM AttendanceRecord WHERE periodId = :periodId AND date = :date")
    suspend fun getAttendanceForPeriod(periodId: Long, date: LocalDate) : AttendanceRecord?

    @Query("SELECT * FROM AttendanceRecord")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>>
}