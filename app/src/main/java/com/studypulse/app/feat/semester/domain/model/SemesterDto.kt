package com.studypulse.app.feat.semester.domain.model

import java.time.LocalDate

data class SemesterDto(
    val id: String? = null,
    val name: String? = null,
    val year: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val minAttendance: Int = 0,
    val current: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun Semester.toDto() =
    SemesterDto(
        id = id,
        name = name.toString(),
        year = year,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        current = isCurrent,
        createdAt = createdAt,
        minAttendance = minAttendance
    )

fun SemesterDto.toDomain() =
    Semester(
        id = id ?: "",
        name = SemesterName.valueOf(name ?: "OTHER"),
        year = year ?: 0,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        isCurrent = current,
        createdAt = createdAt,
        minAttendance = minAttendance
    )