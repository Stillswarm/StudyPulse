package com.studypulse.app.common.util

import java.time.LocalDate
import java.time.YearMonth

object CalendarUtils {

    fun buildMonthGrid(
        yearMonth: YearMonth,
        selectedDate: LocalDate? = null,
        eventDates: Set<LocalDate>
    ): List<DayCellInfo> {
        val firstOfMonth = yearMonth.atDay(1)

        val daysInMonth = yearMonth.lengthOfMonth()
        val today = LocalDate.now()

        val totalCells = 42     // 7 * 6
        val grid = mutableListOf<DayCellInfo>()

        // leading blanks
        for (i in 0 until firstOfMonth.dayOfWeek.value - 1) {
            grid.add(DayCellInfo(dayOfMonth = null, date = null))
        }

        // add all proper dates
        for (day in 1..daysInMonth) {
            val currentDate = yearMonth.atDay(day)
            val isToday = currentDate == today
            val isSelected = currentDate == selectedDate
            val hasEvent = eventDates.contains(currentDate)
            grid.add(
                DayCellInfo(
                    dayOfMonth = day,
                    date = currentDate,
                    isToday = isToday,
                    isSelected = isSelected,
                    hasEvent = hasEvent
                )
            )
        }

        // trailing blanks
        while (grid.size < totalCells) {
            grid.add(DayCellInfo(dayOfMonth = null, date = null))
        }

        return grid
    }
}

/**
 * Data class representing one cell in the 7×6 calendar grid.
 *
 * @param dayOfMonth  The numeric day (1..31) if this cell is in the current month, or null if it’s a blank.
 * @param date        The full LocalDate (e.g. 2025-06-18) if in-month, or null if blank.
 * @param isToday     True if this date == today (LocalDate.now()).
 * @param isSelected  True if this date == the currently selected date.
 * @param hasEvent    True if there’s an attendance mark (you’ll draw a dot) on this date.
 */
data class DayCellInfo(
    val dayOfMonth: Int?,
    val date: LocalDate?,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val hasEvent: Boolean = false
)