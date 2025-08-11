package com.studypulse.app.feat.attendance.courses.domain.model

import androidx.annotation.Keep

@Keep
data class CourseDto(
    val id: String? = null,
    val courseName: String? = null,
    val courseCode: String? = null,
    val instructor: String? = null,
    val semesterId: String? = null,
    val minAttendance: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

fun CourseDto.toDomain() =
    Course(
        id = id ?: "",
        courseName = courseName ?: "",
        courseCode = courseCode ?: "",
        instructor = instructor ?: "",
        semesterId = semesterId ?: "",
        createdAt = createdAt,
        minAttendance = minAttendance
    )

fun Course.toDto() =
    CourseDto(
        id = id,
        courseName = courseName,
        courseCode = courseCode,
        instructor = instructor,
        createdAt = createdAt,
        semesterId = semesterId,
        minAttendance = minAttendance
    )