package com.studypulse.app.feat.attendance.schedule.presentation

import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period

data class ScheduleScreenState(
    val currentDay: Day,
    val courseId: String?,
    val schedule: List<Period>,
    val showDeleteDialog: Boolean,
    val periodIdToDelete: String?,
)