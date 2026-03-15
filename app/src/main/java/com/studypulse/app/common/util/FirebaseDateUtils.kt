package com.studypulse.app.common.util

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneOffset

object FirebaseDateUtils {

    fun LocalDate.toTimestamp(): Timestamp {
        return Timestamp(this.atStartOfDay().toInstant(ZoneOffset.UTC))
    }

    fun Timestamp.toLocalDate(): LocalDate {
        return this.toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
    }
}
