package com.studypulse.app.feat.attendance.schedule.presentation

import com.studypulse.app.feat.attendance.schedule.data.Day
import com.studypulse.app.feat.attendance.schedule.data.Period

data class ScheduleScreenState(
    val currentDay: Day,
    val courseId: Long?,
    val schedule: List<Period>
)