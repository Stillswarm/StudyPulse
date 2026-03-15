package com.studypulse.feat.attendance.courses.presentation.course_details

import com.studypulse.feat.attendance.courses.domain.model.Course

data class CourseDetailsState(
    val isLoading: Boolean = false,
    val course: Course? = null,
)
