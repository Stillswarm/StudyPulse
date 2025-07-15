package com.studypulse.app.feat.attendance.courses.domain

data class CourseSummary(
    val id: String,
    val courseId: String,
    val totalRecords: Int,
    val presentRecords: Int,
    val absentRecords: Int,
    val unmarkedRecords: Int,
)
