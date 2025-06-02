package com.studypulse.app.feat.attendance.courses.presentation.add_period

import com.studypulse.app.feat.attendance.schedule.data.Day

data class AddPeriodState(
    val selectedDay: Day,
    val startTimeHour: Int,
    val startTimeMinute: Int,
    val endTimeHour: Int,
    val endTimeMinute: Int,
)