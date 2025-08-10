package com.studypulse.app.feat.attendance.courses.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalTime

@Parcelize
data class Period(
    val id: String = "",
    val semesterId: String,
    val courseId: String,
    val courseName: String,
    val day: Day,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

fun Period.overlapsWith(other: Period): Boolean {
    // only compare periods on the same day
    if (this.day != other.day) return false

    // intervals overlap if startA < endB AND endA > startB
    return this.startTime.isBefore(other.endTime) &&
            this.endTime.isAfter(other.startTime)
}