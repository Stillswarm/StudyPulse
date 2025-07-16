package com.studypulse.app.feat.attendance.attendance.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

data class AttendanceRecord(
    val id: String = "",
    val userId: String = "",
    val semesterId: String = "",
    val periodId: String,
    val courseId: String,
    val date: LocalDate,
    val status: AttendanceStatus,
    val processed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    CANCELLED,
    UNMARKED,
}
