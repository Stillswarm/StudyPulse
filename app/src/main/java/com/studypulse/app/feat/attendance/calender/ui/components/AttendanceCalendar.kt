package com.studypulse.app.feat.attendance.calender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.common.util.CalendarUtils.buildMonthGrid
import com.studypulse.app.common.util.DayCellInfo
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.common.util.getAbbreviatedName
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.ui.theme.Gold
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AttendanceCalendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    eventDates: Set<LocalDate>,
    onDateSelected: (LocalDate?) -> Unit,
    onMonthChanged: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // calendar month header
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                contentDescription = "Previous Month",
                modifier = Modifier.noRippleClickable { onMonthChanged(yearMonth.minusMonths(1)) }
            )

            Text(
                text = yearMonth.month.toString() + " " + yearMonth.year,
                fontSize = 16.sp,
                lineHeight = 20.sp,
            )

            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "Previous Month",
                modifier = Modifier.noRippleClickable { onMonthChanged(yearMonth.plusMonths(1)) }
            )
        }

        // calendar content
        // weekday header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Day.entries.forEach { day ->
                Text(
                    text = day.name.convertToSentenceCase().getAbbreviatedName(),
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }

        // 7 x 6 grid
        val gridCells = remember(yearMonth, selectedDate, eventDates) {
            buildMonthGrid(yearMonth, selectedDate, eventDates)
        }

        DayGrid(
            gridCells = gridCells,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
fun DayGrid(
    gridCells: List<DayCellInfo>,
    onDateSelected: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = modifier
            .fillMaxWidth(),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(gridCells) { index, dayCell ->
            DayCell(
                info = dayCell,
                onClick = { date ->
                    if (date != null) {
                        onDateSelected(date)
                    }
                }
            )
        }
    }
}

@Composable
fun DayCell(
    info: DayCellInfo,
    onClick: (LocalDate?) -> Unit,
) {
    val cellSize = 48.dp
    val highlightBoxSize = 40.dp
    val dotSize = 4.dp

    Box(
        modifier = Modifier
            .size(cellSize)
            .noRippleClickable { onClick(info.date) },
        contentAlignment = Alignment.Center
    ) {
        if (info.isToday && info.dayOfMonth != null) {
            Box(
                modifier = Modifier
                    .size(highlightBoxSize)
                    .drawBehind {
                        // Fill
                        drawRoundRect(
                            color = Gold.copy(alpha = 0.1f),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                        // Border
                        drawRoundRect(
                            color = Gold,
                            style = Stroke(width = 2.dp.toPx()),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
            )
        }

        if (info.isSelected && info.dayOfMonth != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .size(highlightBoxSize)
                    .background(color = Gold)
            )
        }

        if (info.dayOfMonth != null) {
            Text(
                text = info.dayOfMonth.toString(),
                fontSize = 14.sp,
                color = when {
                    info.isSelected -> Color.White
                    info.isToday -> Color.Unspecified
                    else -> Color.Black
                }
            )
        }

        if (info.hasEvent && info.dayOfMonth != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 4.dp)
                    .size(dotSize)
                    .background(color = if (info.isSelected) Gold else Color(0xFF007AFF))
            )
        }
    }
}