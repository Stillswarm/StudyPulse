package com.studypulse.app.feat.attendance.schedule.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity
data class Period(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long,
    val courseName: String,
    val day: Day,
    val startTime: LocalTime,
    val endTime: LocalTime,
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
