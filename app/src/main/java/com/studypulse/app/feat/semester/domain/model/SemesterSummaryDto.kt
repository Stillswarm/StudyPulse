package com.studypulse.app.feat.semester.domain.model

data class SemesterSummaryDto(
    val id: String = "",
    val semesterId: String = "",
    val totalRecords: Int = 0,
    val presentRecords: Int = 0,
    val absentRecords: Int = 0,
    val unmarkedRecords: Int = 0,
)

fun SemesterSummary.toDto() =
    SemesterSummaryDto(
        totalRecords = totalRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        semesterId = semesterId,
    )

fun SemesterSummaryDto.toDomain() =
    SemesterSummary(
        totalRecords = totalRecords,
        presentRecords = presentRecords,
        absentRecords = absentRecords,
        unmarkedRecords = unmarkedRecords,
        id = id,
        semesterId = semesterId,
    )
