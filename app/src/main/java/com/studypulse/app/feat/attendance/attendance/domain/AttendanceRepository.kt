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
    fun getUnmarkedClassesCount(): Flow<Int>
}