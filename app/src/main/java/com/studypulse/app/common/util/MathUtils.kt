package com.studypulse.app.common.util

import kotlin.math.ceil
import kotlin.math.floor

object MathUtils {
    const val INF = Integer.MAX_VALUE

    fun calculatePercentage(present: Int, total: Int): Int {
        return if (total == 0) INF else (present * 100) / total
    }

    fun maxSkipsAllowed(
        present: Int,
        absent: Int,
        unmarked: Int,
        minPercent: Int
    ): Int {
        /*
            total classes = t, present in = p, attendance required = a

            assume we can skip x classes, then afterwards
            total classes = t + x, present = p

            and, 100p / (t + x) >= a
              => 100p >= a(t + x)
              => t + x <= 100 * p/a
              => x <= 100p/a - t
              => x = floor(100p/a - t)
         */

        val total = present + absent + unmarked
        return floor(100.0 * present / minPercent - total).toInt()
    }

    fun minClassesRequired(
        present: Int,
        absent: Int,
        unmarked: Int,
        minPercent: Int
    ): Int {
        /*
            total so far = t, present in = p, att. required = a

            assume we attend all of the next x classes, then
            total classes = t + x, present in = p + x,

            and, 100(p + x) / (t + x) >= a
              on solving, x(min) = ceil((at - 100p) / (100 - a)), when a != 100
                      */
        if (minPercent == 100) {
            return INF
        }

        val total = present + absent + unmarked
        return ceil((minPercent * total - 100 * present) / (100.0 - minPercent)).toInt()
    }
}