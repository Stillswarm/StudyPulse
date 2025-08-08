package com.studypulse.app

import com.studypulse.app.feat.semester.domain.model.Semester
import com.studypulse.app.feat.semester.domain.model.SemesterName
import java.time.LocalDate

val fixedDateBefore = LocalDate.of(2025, 1,1)
val fixedDateAfter = LocalDate.of(2025, 12,1)

val sem1 = Semester(
    id = "sem1",
    name = SemesterName.AUTUMN,
    year = 5,
    startDate = fixedDateBefore,
    endDate = fixedDateAfter,
    minAttendance = 80,
    isCurrent = true,
    createdAt = 1_000
)