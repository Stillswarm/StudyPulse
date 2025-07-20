package com.studypulse.app.feat.attendance.calender.ui

import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import java.time.LocalDate
import java.time.YearMonth

data class AttendanceCalendarScreenState(
    val semesterStartDate: LocalDate = LocalDate.now(),
    val semesterEndDate: LocalDate = LocalDate.now(),
    val yearMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val periodsList: List<PeriodWithAttendance> = emptyList(),
    val showBottomSheet: Boolean = false
)

data class PeriodWithAttendance(
    val period: Period,
    val attendanceRecord: AttendanceRecord?
)