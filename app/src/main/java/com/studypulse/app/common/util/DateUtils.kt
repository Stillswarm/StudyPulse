package com.studypulse.app.common.util

import com.studypulse.app.feat.attendance.courses.domain.model.Day

fun Day.toCalendarDay(): Long {
    return when (this) {
        Day.MONDAY -> 1L
        Day.TUESDAY -> 2L
        Day.WEDNESDAY -> 3L
        Day.THURSDAY -> 4L
        Day.FRIDAY -> 5L
        Day.SATURDAY -> 6L
        Day.SUNDAY -> 7L
    }
}