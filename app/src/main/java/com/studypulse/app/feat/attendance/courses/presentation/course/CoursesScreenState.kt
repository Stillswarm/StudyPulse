package com.studypulse.app.feat.attendance.courses.presentation.course

import com.studypulse.app.feat.attendance.courses.data.Course

data class CoursesScreenState(
    val isLoading: Boolean,
    val courses: List<Course>
)