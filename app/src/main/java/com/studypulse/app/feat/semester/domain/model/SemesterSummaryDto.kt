package com.studypulse.app.feat.semester.domain.model

data class SemesterSummaryDto(
    val id: String = "",
    val semesterId: String = "",
    val cancelledRecords: Int = 0,
    val presentRecords: Int = 0,
    val absentRecords: Int = 0,
    val unmarkedRecords: Int = 0,
    val minAttendance: Int = 0,
)

fun SemesterSummary.toDto() =
    SemesterSummaryDto(
        cancelledRecords = cancelledRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        semesterId = semesterId,
        minAttendance = minAttendance
    )

fun SemesterSummaryDto.toDomain() =
    SemesterSummary(
        cancelledRecords = cancelledRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        semesterId = semesterId,
        minAttendance = minAttendance
    )
