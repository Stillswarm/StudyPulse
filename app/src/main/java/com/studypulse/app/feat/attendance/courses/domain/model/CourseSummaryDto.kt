package com.studypulse.app.feat.attendance.courses.domain.model

import com.studypulse.app.feat.attendance.courses.domain.CourseSummary

data class CourseSummaryDto(
    val id: String = "",
    val courseId: String = "",
    val totalRecords: Int = 0,
    val presentRecords: Int = 0,
    val absentRecords: Int = 0,
    val unmarkedRecords: Int = 0,
)

fun CourseSummary.toDto() =
    CourseSummaryDto(
        totalRecords = totalRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        courseId = courseId
    )

fun CourseSummaryDto.toDomain() =
    CourseSummary(
        totalRecords = totalRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        courseId = courseId,
    )
