package com.studypulse.app.feat.semester.domain.model

data class SemesterSummary(
    val id: String,
    val semesterId: String,
    val totalRecords: Int,
    val presentRecords: Int,
    val absentRecords: Int,
    val unmarkedRecords: Int,
)
