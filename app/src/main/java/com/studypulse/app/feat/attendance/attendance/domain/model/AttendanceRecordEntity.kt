package com.studypulse.app.feat.attendance.attendance.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val periodId: String,
    val courseId: String,
    val date: LocalDate,
    val status: AttendanceStatus,
    val createdAt: Long = System.currentTimeMillis(),
)

fun AttendanceRecordEntity.toDomain() =
    AttendanceRecord(
        id = id.toString(),
        periodId = periodId,
        courseId = courseId,
        date = date,
        status = status,
        createdAt = createdAt
    )

fun AttendanceRecord.toEntity() =
    AttendanceRecordEntity(
        id = id.toLong(),
        periodId = periodId,
        courseId = courseId,
        date = date,
        status = status,
        createdAt = createdAt
    )