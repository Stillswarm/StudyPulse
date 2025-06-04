package com.studypulse.app.feat.attendance.attendance.data

import com.studypulse.app.feat.attendance.attendance.domain.AttendanceDao
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class RoomAttendanceRepositoryImpl(
    private val attendanceDao: AttendanceDao
) : AttendanceRepository {
    override suspend fun upsertAttendance(attendanceRecord: AttendanceRecord) {
        attendanceDao.upsertAttendance(attendanceRecord)
    }

    override fun getAttendanceByDate(date: LocalDate): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceByDate(date)
    }

    override suspend fun getAttendanceForPeriodAndDate(periodId: Long, date: LocalDate): AttendanceRecord? {
        return attendanceDao.getAttendanceForPeriod(periodId, date)
    }
}