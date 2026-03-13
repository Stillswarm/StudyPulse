package com.studypulse.app.feat.attendance.schedule.presentation

import com.studypulse.app.feat.attendance.courses.domain.model.Period
import java.time.DayOfWeek

data class ScheduleScreenState(
    val currentDay: DayOfWeek,
    val courseId: String?,
    val courseCode: String?,
    val schedule: List<Period>,
    val showDeleteDialog: Boolean,
    val periodIdToDelete: String?,
)