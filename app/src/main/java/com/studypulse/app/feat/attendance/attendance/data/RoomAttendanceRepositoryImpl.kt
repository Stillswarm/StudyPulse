package com.studypulse.app.feat.attendance.attendance.data

import com.studypulse.app.feat.attendance.attendance.domain.AttendanceDao
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.toDomain
import com.studypulse.app.feat.attendance.attendance.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomAttendanceRepositoryImpl(
    private val attendanceDao: AttendanceDao
) : AttendanceRepository {
    override suspend fun upsertAttendance(attendanceRecord: AttendanceRecord) {
        attendanceDao.upsertAttendance(attendanceRecord.toEntity())
    }

    override fun getAttendanceByDate(date: LocalDate): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceByDate(date).map {
            it.map { att -> att.toDomain() }
        }
    }

    override suspend fun getAttendanceForPeriodAndDate(
        periodId: String,
        date: LocalDate
    ): AttendanceRecord? {
        return attendanceDao.getAttendanceForPeriod(periodId.toLong(), date)?.toDomain()
    }

    override fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAllAttendanceRecords().map { it.map { att -> att.toDomain() } }
    }

    override fun getAttendanceGroupedByCourse(): Flow<Map<String, List<AttendanceRecord>>> {
        return attendanceDao.getAllAttendanceRecords().map { list -> list.map{ it.toDomain() }.groupBy { it.courseId } }
    }

    override suspend fun getDatesWithUnmarkedAttendance(
        semesterId: String,
        monthStartDate: LocalDate,
        endDate: LocalDate,
    ): Result<Set<LocalDate>> {
        TODO("Not yet implemented")
    }

    override suspend fun hasPendingAttendanceForDate(semesterId: String, date: LocalDate): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertManyAttendance(records: List<AttendanceRecord>): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAttendance(attendanceRecordId: String): Result<Unit> {
        TODO("Not yet implemented")
    }
}