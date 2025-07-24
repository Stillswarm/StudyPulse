package com.studypulse.app.feat.attendance.attendance.domain

import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AttendanceRepository {
    suspend fun upsertAttendance(attendanceRecord: AttendanceRecord)
    fun getAttendanceByDate(date: LocalDate): Flow<List<AttendanceRecord>>
    suspend fun getAttendanceForPeriodAndDate(periodId: String, date: LocalDate) : AttendanceRecord?
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>>
    fun getAttendanceGroupedByCourse(): Flow<Map<String, List<AttendanceRecord>>>
    suspend fun hasPendingAttendanceForDate(semesterId: String, date: LocalDate): Result<Boolean>
    suspend fun upsertManyAttendance(records: List<AttendanceRecord>): Result<Unit>
    suspend fun getDatesWithUnmarkedAttendance(
        semesterId: String,
        monthStartDate: LocalDate,
        endDate: LocalDate
    ): Result<Set<LocalDate>>
}