package com.studypulse.app

import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.semester.domain.model.Semester
import com.studypulse.app.feat.semester.domain.model.SemesterName
import java.time.LocalDate
import java.time.LocalTime

val fixedDate = LocalDate.of(2025, 1, 4)    // saturday

val mockPeriod = Period(
    id = "p1",
    semesterId = "sem1",
    courseId = "c1",
    courseName = "Course 1",
    day = Day.SATURDAY,
    startTime = LocalTime.parse("09:00"),
    endTime = LocalTime.parse("10:00"),
)

val mockAttendance = AttendanceRecord(
    periodId = "p1",
    courseId = "c1",
    date = fixedDate,
    status = AttendanceStatus.PRESENT,
)

val sem1 = Semester(
    id = "sem1",
    name = SemesterName.AUTUMN,
    year = 2025,
    startDate = LocalDate.parse("2025-01-01"),
    endDate = LocalDate.parse("2025-12-31"),
    minAttendance = 75,
    isCurrent = false,
    createdAt = 0L
)

val sem2 = Semester(
    id = "sem2",
    name = SemesterName.SPRING,
    year = 2026,
    startDate = LocalDate.parse("2026-01-10"),
    endDate = LocalDate.parse("2026-05-15"),
    minAttendance = 75,
    isCurrent = true,
    createdAt = 0L
)

val period1 = Period(
    id = "p1",
    semesterId = "sem1",
    courseId = "c1",
    courseName = "Course 1",
    day = Day.FRIDAY,
    startTime = LocalTime.of(9, 0),
    endTime = LocalTime.of(10, 0),
    createdAt = 10000
)

val period2 = Period(
    id = "p2",
    semesterId = "sem2",
    courseId = "c2",
    courseName = "Course 2",
    day = Day.WEDNESDAY,
    startTime = LocalTime.of(14, 50),
    endTime = LocalTime.of(16, 0),
    createdAt = 10000
)

val course1 = Course(
    id = "c1",
    courseName = "Course 1",
    courseCode = "CR100",
    instructor = "Mr. XYZ",
    semesterId = "sem1",
    minAttendance = 50,
    createdAt = 1_5000
)

val course2 = Course(
    id = "c2",
    courseName = "Course 2",
    courseCode = "CR200",
    instructor = "Mr. ABC",
    semesterId = "sem2",
    minAttendance = 85,
    createdAt = 1_5000
)

val courseSummary1 = CourseSummary(
    id = "cs1",
    userId = "u1",
    semesterId = "sem1",
    courseId = "c1",
    courseName = "Course 1",
    cancelledRecords = 15,
    presentRecords = 56,
    absentRecords = 10,
    unmarkedRecords = 2,
    minAttendance = 75
)

val courseSummary2 = CourseSummary(
    id = "cs2",
    userId = "u1",
    semesterId = "sem1",
    courseId = "c1",
    courseName = "Course 1",
    cancelledRecords = 0,
    presentRecords = 10,
    absentRecords = 0,
    unmarkedRecords = 0,
    minAttendance = 80
)