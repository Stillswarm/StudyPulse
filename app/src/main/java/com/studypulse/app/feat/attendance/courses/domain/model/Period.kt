package com.studypulse.app.feat.attendance.courses.domain.model

import java.time.LocalTime

data class Period(
    val id: String = "",
    val courseId: String,
    val courseName: String,
    val day: Day,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}
