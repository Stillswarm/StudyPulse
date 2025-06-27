package com.studypulse.app.feat.attendance.courses.domain.model

data class CourseDto(
    val id: String? = null,
    val courseName: String? = null,
    val courseCode: String? = null,
    val instructor: String? = null,
    val semesterId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun CourseDto.toDomain() =
    Course(
        id = id ?: "",
        courseName = courseName ?: "",
        courseCode = courseCode ?: "",
        instructor = instructor ?: "",
        semesterId = semesterId ?: "",
        createdAt = createdAt
    )

fun Course.toDto() =
    CourseDto(
        id = id,
        courseName = courseName,
        courseCode = courseCode,
        instructor = instructor,
        createdAt = createdAt,
        semesterId = semesterId
    )