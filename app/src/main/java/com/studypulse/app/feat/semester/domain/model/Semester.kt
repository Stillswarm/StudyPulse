package com.studypulse.app.feat.semester.domain.model

import java.time.LocalDate

data class Semester(
    val id: String,
    val name: SemesterName,
    val year: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val minAttendance: Int,
    val isCurrent: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)

enum class SemesterName {
    AUTUMN, SPRING, OTHER
}
