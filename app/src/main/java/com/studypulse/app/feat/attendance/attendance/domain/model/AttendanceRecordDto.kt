package com.studypulse.app.feat.attendance.attendance.domain.model

import java.time.LocalDate

data class AttendanceRecordDto(
    val id: String = "",
    val periodId: String? = null,
    val courseId: String? = null,
    val date: String = LocalDate.now().toString(),
    val status: String = AttendanceStatus.PRESENT.name,
    val createdAt: Long = System.currentTimeMillis()
)

fun AttendanceRecord.toDto() = AttendanceRecordDto(
    id = id,
    periodId = periodId,
    courseId = courseId,
    date = date.toString(),
    status = status.name,
    createdAt = createdAt
)

fun AttendanceRecordDto.toDomain() = AttendanceRecord(
    id = id,
    periodId = periodId ?: "",
    courseId = courseId ?: "",
    date = LocalDate.parse(date),
    status = AttendanceStatus.valueOf(status),
    createdAt = createdAt
)