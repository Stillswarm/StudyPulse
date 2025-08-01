package com.studypulse.app.feat.attendance.courses.domain

data class CourseSummary(
    val id: String,
    val userId: String,
    val semesterId: String,
    val courseId: String,
    val courseName: String,
    val cancelledRecords: Int,
    val presentRecords: Int,
    val absentRecords: Int,
    val unmarkedRecords: Int,
    val minAttendance: Int,
) {
    val totalClasses = presentRecords + absentRecords + unmarkedRecords
}
