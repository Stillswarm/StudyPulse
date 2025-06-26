package com.studypulse.app.feat.attendance.courses.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity
data class PeriodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: String,
    val courseName: String,
    val day: Day,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val createdAt: Long = System.currentTimeMillis()
)

fun PeriodEntity.toPeriod() = 
    Period(
        id = id.toString(),
        courseId = courseId,
        courseName = courseName,
        day = day,
        startTime = startTime,
        endTime = endTime,
        createdAt = createdAt
    )

fun Period.toPeriodEntity() =
    PeriodEntity(
        id = id.toLongOrNull() ?: 0,
        courseId = courseId,
        courseName = courseName,
        day = day,
        startTime = startTime,
        endTime = endTime,
        createdAt = createdAt
    )