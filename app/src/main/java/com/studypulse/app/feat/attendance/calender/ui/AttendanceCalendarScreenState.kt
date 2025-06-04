package com.studypulse.app.feat.attendance.calender.ui

import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRecord
import com.studypulse.app.feat.attendance.schedule.data.Period
import java.time.LocalDate
import java.time.YearMonth

data class AttendanceCalendarScreenState(
    val yearMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val periodsList: List<PeriodWithAttendance> = emptyList(),
)

data class PeriodWithAttendance(
    val period: Period,
    val attendanceRecord: AttendanceRecord?
)