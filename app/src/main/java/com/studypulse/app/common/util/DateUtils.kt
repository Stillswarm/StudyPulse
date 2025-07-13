package com.studypulse.app.common.util

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalTime.to12HourString() : String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("hh:mm a")
    return this.format(formatter)
}

fun LocalDate.toFullString(): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")
    return this.format(formatter)
}

fun Long.toLocalTime(): LocalTime {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
}

fun Long.toLocalDate(): LocalDate {
    return Instant
        .ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun LocalDate.toTimestamp(): Timestamp {
    return Timestamp(this.atStartOfDay().toInstant(ZoneOffset.UTC))
}

fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
}