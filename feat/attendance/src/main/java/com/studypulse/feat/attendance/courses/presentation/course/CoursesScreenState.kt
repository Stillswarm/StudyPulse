package com.studypulse.feat.attendance.courses.presentation.course

import com.studypulse.feat.attendance.courses.domain.model.Course

data class CoursesScreenState(
    val isLoading: Boolean,
    val courses: List<Course>
)