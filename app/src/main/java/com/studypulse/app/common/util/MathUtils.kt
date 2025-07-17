package com.studypulse.app.common.util

import kotlin.math.max

object MathUtils {
    fun calculatePercentage(part: Int, total: Int): Int {
        return if (total == 0) 0 else (part * 100) / total
    }

    fun maxSkipsAllowed(
        present: Int,
        absent: Int,
        unmarked: Int,
        minPercent: Int
    ): Int {
        val total = present + absent + unmarked
        // how many presents you need across all total classes
        val requiredPresents = (minPercent / 100.0 * total).toInt()
        // max you can convert from unmarked into absences:
        return max(0, (present + unmarked) - requiredPresents)
    }
}