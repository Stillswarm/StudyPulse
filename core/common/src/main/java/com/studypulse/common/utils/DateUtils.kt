package com.studypulse.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    fun LocalTime.to12HourString() : String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return this.format(formatter)
    }

    fun LocalDate.toStandardString(): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
        return this.format(formatter)
    }

    fun LocalDate.toFullString(): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
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

    fun formatWithLeadingZero(number: Int): String {
        // %02d means:
        // d: format as a decimal integer
        // 2: width of 2 characters
        // 0: pad with leading zeros
        return String.format(Locale.getDefault(), "%02d", number)
    }

}