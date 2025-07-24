package com.studypulse.app.feat.attendance.attendance.presentation.home

import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.semester.domain.model.Semester

data class AttendanceScreenState(
    val isLoading: Boolean = false,
    val unmarkedCount: Int = 0,
    val fullAttendanceCount: Int = 0,
    val lowAttendanceCount: Int = 0,
    val attendancePercentage: Int = 0,
    val courseWiseSummaries: List<CourseSummary> = emptyList(),
    val semesterList: List<Semester> = emptyList(),
    val activeSemester: Semester? = null,
)