package com.studypulse.app.feat.attendance.attendance.domain.model

import com.google.firebase.Timestamp
import com.studypulse.app.common.util.toLocalDate
import com.studypulse.app.common.util.toTimestamp
import java.time.LocalDate

data class AttendanceRecordDto(
    val id: String = "",
    val periodId: String? = null,
    val courseId: String? = null,
    val date: Timestamp? = null,
    val status: String = AttendanceStatus.UNMARKED.name,
    val createdAt: Long = System.currentTimeMillis()
)

fun AttendanceRecord.toDto() = AttendanceRecordDto(
    id = id,
    periodId = periodId,
    courseId = courseId,
    date = date.toTimestamp(),
    status = status.name,
    createdAt = createdAt
)

fun AttendanceRecordDto.toDomain() = AttendanceRecord(
    id = id,
    periodId = periodId ?: "",
    courseId = courseId ?: "",
    date = date?.toLocalDate() ?: LocalDate.now(),
    status = AttendanceStatus.valueOf(status),
    createdAt = createdAt
)