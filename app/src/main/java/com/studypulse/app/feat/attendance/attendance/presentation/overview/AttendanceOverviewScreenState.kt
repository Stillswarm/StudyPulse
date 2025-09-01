package com.studypulse.app.feat.attendance.attendance.presentation.overview

import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.model.Course

data class AttendanceOverviewScreenState(
    val loading: Boolean = true,
    val courseWiseSummaries: Map<Course, CourseSummary> = emptyMap(),
    val topBarTitle: String = "Attendance Overview"
)
