package com.studypulse.app.feat.attendance.attendance.domain.use_cases

import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.calender.ui.PeriodWithAttendance
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.time.LocalDate

interface GetAllUnmarkedPeriodsUseCase {
    operator fun invoke(): Flow<List<PeriodWithAttendance>>
}

class GetAllUnmarkedPeriodsUseCaseImpl(
    private val attendanceRepository: AttendanceRepository,
    private val periodRepository: PeriodRepository,
) : GetAllUnmarkedPeriodsUseCase {
    override fun invoke(): Flow<List<PeriodWithAttendance>> {
        return attendanceRepository.getUnmarkedRecordsFlow(upto = LocalDate.now())
            .mapNotNull { records ->
                records.mapNotNull { record ->
                    periodRepository.getPeriodById(record.periodId).getOrNull()?.let {
                        PeriodWithAttendance(
                            period = it,
                            attendanceRecord = record
                        )
                    }
                }
            }
    }
}