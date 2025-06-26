package com.studypulse.app.feat.attendance.attendance.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Entity
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val periodId: String,
    val courseId: String,
    val date: LocalDate,
    val status: AttendanceStatus,
)

@Serializable
enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    CANCELLED,
}
