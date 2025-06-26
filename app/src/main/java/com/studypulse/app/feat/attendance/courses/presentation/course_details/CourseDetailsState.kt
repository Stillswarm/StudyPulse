package com.studypulse.app.feat.attendance.courses.presentation.course_details

import com.studypulse.app.feat.attendance.courses.domain.model.Course

data class CourseDetailsState(
    val isLoading: Boolean = false,
    val course: Course? = null,
)
