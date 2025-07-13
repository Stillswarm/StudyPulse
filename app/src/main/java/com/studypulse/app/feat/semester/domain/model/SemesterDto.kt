package com.studypulse.app.feat.semester.domain.model

import com.google.firebase.Timestamp
import com.studypulse.app.common.util.toLocalDate
import com.studypulse.app.common.util.toTimestamp
import java.time.LocalDate

data class SemesterDto(
    val id: String? = null,
    val name: String? = null,
    val year: Int? = null,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val minAttendance: Int = 0,
    val current: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun Semester.toDto() =
    SemesterDto(
        id = id,
        name = name.toString(),
        year = year,
        startDate = startDate.toTimestamp(),
        endDate = endDate.toTimestamp(),
        current = isCurrent,
        createdAt = createdAt,
        minAttendance = minAttendance
    )

fun SemesterDto.toDomain() =
    Semester(
        id = id ?: "",
        name = SemesterName.valueOf(name ?: "OTHER"),
        year = year ?: 0,
        startDate = startDate?.toLocalDate() ?: LocalDate.now(),
        endDate = endDate?.toLocalDate() ?: LocalDate.now(),
        isCurrent = current,
        createdAt = createdAt,
        minAttendance = minAttendance
    )