package com.studypulse.app.feat.attendance.courses.domain.model

data class Course(
    val id: String = "",
    val courseName: String,
    val courseCode: String,
    val instructor: String,
    val semesterId: String,
    val createdAt: Long = System.currentTimeMillis(),
)