package com.studypulse.app.feat.attendance.courses.presentation.course

import com.studypulse.app.feat.attendance.courses.domain.model.Course

data class CoursesScreenState(
    val isLoading: Boolean,
    val courses: List<Course>
)