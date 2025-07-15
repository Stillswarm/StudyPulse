package com.studypulse.app.feat.attendance.attendance.presentation.home

data class AttendanceScreenState(
    val isLoading: Boolean = false,
    val unmarkedCount: Int = 0,
    val fullAttendanceCount: Int = 0,
    val lowAttendanceCount: Int = 0,
    val attendancePercentage: Int = 0,
)