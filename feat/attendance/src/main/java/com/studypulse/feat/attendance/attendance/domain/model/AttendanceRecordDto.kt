package com.studypulse.feat.attendance.attendance.domain.model

import androidx.annotation.Keep
import com.studypulse.feat.attendance.data.FirebaseDateUtils.toLocalDate
import com.studypulse.feat.attendance.data.FirebaseDateUtils.toTimestamp
import com.google.firebase.Timestamp
import java.time.LocalDate

@Keep
data class AttendanceRecordDto(
    val id: String = "",
    val userId: String? = null,
    val semesterId: String? = null,
    val periodId: String? = null,
    val courseId: String? = null,
    val date: Timestamp? = null,
    val processed: Boolean = false,
    val status: String = AttendanceStatus.UNMARKED.name,
    val createdAt: Long = System.currentTimeMillis()
)

fun AttendanceRecord.toDto() = AttendanceRecordDto(
    id = id,
    userId = userId,
    semesterId = semesterId,
    periodId = periodId,
    courseId = courseId,
    date = date.toTimestamp(),
    status = status.name,
    createdAt = createdAt,
    processed = processed
)

fun AttendanceRecordDto.toDomain() = AttendanceRecord(
    id = id,
    userId = userId ?: "",
    semesterId = semesterId ?: "",
    periodId = periodId ?: "",
    courseId = courseId ?: "",
    date = date?.toLocalDate() ?: LocalDate.now(),
    status = AttendanceStatus.valueOf(status),
    createdAt = createdAt,
    processed = processed
)