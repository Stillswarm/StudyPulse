package com.studypulse.app.feat.attendance.courses.presentation.add_period

import com.studypulse.app.feat.attendance.courses.domain.model.Day

data class AddPeriodScreenState(
    val periodId: String = "",
    val selectedDay: Day,
    val startTimeHour: Int,
    val startTimeMinute: Int,
    val endTimeHour: Int,
    val endTimeMinute: Int,
    val showStartTimePicker: Boolean = false,
    val showEndTimePicker: Boolean = false,
    val showConfirmationPopup: Boolean = false,
    val granted: Boolean = false,    // set when user wants to submit even after the popup warning
    val timeRange: String = "",
)