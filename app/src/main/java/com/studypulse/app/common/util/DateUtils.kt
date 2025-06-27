package com.studypulse.app.common.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun LocalTime.to12HourString() : String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
    return this.format(formatter)
}

fun LocalDate.toFullString(): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")
    return this.format(formatter)
}

fun Long.toLocalDate(): LocalDate {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}