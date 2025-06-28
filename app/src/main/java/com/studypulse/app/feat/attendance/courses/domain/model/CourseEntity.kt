package com.studypulse.app.feat.attendance.courses.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
    val remoteId: String?,
    val courseName: String,
    val courseCode: String,
    val instructor: String,
    val semesterId: String,
    val minAttendance: Int,
    val createdAt: Long = System.currentTimeMillis()
)

fun CourseEntity.toDomain() = Course(
    id = localId.toString(),
    courseName = courseName,
    courseCode = courseCode,
    instructor = instructor,
    semesterId = semesterId,
    createdAt = createdAt,
    minAttendance = minAttendance
)

fun Course.toEntity() = CourseEntity(
    localId = id.toLongOrNull() ?: 0L,
    courseName = courseName,
    courseCode = courseCode,
    instructor = instructor,
    createdAt = createdAt,
    remoteId = id,
    semesterId = semesterId,
    minAttendance = minAttendance
)
