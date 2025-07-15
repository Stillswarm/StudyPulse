package com.studypulse.app.feat.semester.domain.model

data class SemesterSummary(
    val id: String,
    val semesterId: String,
    val cancelledRecords: Int,
    val presentRecords: Int,
    val absentRecords: Int,
    val unmarkedRecords: Int,
    val minAttendance: Int,
)
