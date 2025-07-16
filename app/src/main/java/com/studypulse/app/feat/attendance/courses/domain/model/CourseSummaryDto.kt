package com.studypulse.app.feat.attendance.courses.domain.model

import com.studypulse.app.feat.attendance.courses.domain.CourseSummary

data class CourseSummaryDto(
    val id: String = "",
    val userId: String = "",
    val semesterId: String = "",
    val courseId: String = "",
    val courseName: String = "",
    val cancelledRecords: Int = 0,
    val presentRecords: Int = 0,
    val absentRecords: Int = 0,
    val unmarkedRecords: Int = 0,
    val minAttendance: Int = 0,
)

fun CourseSummary.toDto() =
    CourseSummaryDto(
        cancelledRecords = cancelledRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        courseId = courseId,
        minAttendance = minAttendance,
        courseName = courseName,
        semesterId = semesterId,
        userId = userId
    )

fun CourseSummaryDto.toDomain() =
    CourseSummary(
        userId = userId,
        semesterId = semesterId,
        cancelledRecords = cancelledRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        courseId = courseId,
        minAttendance = minAttendance,
        courseName = courseName
    )
