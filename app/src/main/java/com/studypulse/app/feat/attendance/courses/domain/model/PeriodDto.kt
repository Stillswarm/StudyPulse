package com.studypulse.app.feat.attendance.courses.domain.model

import java.time.LocalTime

data class PeriodDto(
    val id: String? = null,
    val courseId: String? = null,
    val courseName: String? = null,
    val day: String = Day.MONDAY.name,
    val startTime: String = LocalTime.now().toString(),
    val endTime: String = LocalTime.now().toString(),
    val createdAt: Long = System.currentTimeMillis()
)

fun PeriodDto.toDomain(): Period {
    return Period(
        id = this.id ?: "",
        courseId = this.courseId ?: "",
        courseName = this.courseName ?: "",
        day = Day.valueOf(this.day),
        startTime = LocalTime.parse(this.startTime),
        endTime = LocalTime.parse(this.endTime),
        createdAt = this.createdAt
    )
}

fun Period.toDto(): PeriodDto {
    return PeriodDto(
        id = this.id,
        courseId = this.courseId,
        courseName = this.courseName,
        day = this.day.name,
        startTime = this.startTime.toString(),
        endTime = this.endTime.toString(),
        createdAt = this.createdAt
    )
}