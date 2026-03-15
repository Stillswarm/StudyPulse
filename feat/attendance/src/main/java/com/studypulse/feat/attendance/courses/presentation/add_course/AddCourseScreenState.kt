package com.studypulse.feat.attendance.courses.presentation.add_course

import com.studypulse.core.semester.model.Semester

data class AddCourseScreenState(
    val courseName: String = "",
    val courseCode: String = "",
    val instructor: String = "",
    val minAttendance: String = "",
    val activeSemester: Semester? = null,
    val allSemesters: List<Semester> = emptyList(),
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
)