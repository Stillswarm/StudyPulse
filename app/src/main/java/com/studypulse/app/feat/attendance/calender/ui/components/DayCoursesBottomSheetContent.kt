package com.studypulse.app.feat.attendance.calender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.common.ui.components.noRippleClickable
import com.studypulse.app.common.util.toFullString
import com.studypulse.app.feat.attendance.schedule.data.Period
import java.time.LocalDate

@Composable
fun DayCoursesBottomSheetContent(
    periodList: List<Period>,
    localDate: LocalDate,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = localDate.toFullString(),
                    fontSize = 18.sp,
                    lineHeight = 28.sp
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Class Schedule",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "hide",
                modifier = Modifier.noRippleClickable { onClose() }
            )
        }

        periodList.forEach { periodPresentation ->
            BottomSheetItem(
                period = periodPresentation,
            )
        }
    }
}

@Composable
fun BottomSheetItem(
    period: Period,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = period.courseName,
                fontSize = 16.sp,
                lineHeight = 24.sp
            )

            Text(
                text = "${period.startTime} - ${period.endTime}",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            AttendanceStatusButtonsRow(
                onPresent = {},
                onAbsent = {},
                onCancelled = {}
            )
        }
    }
}

@Composable
fun AttendanceStatusButtonsRow(
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onCancelled: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // present button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(32.dp)
                .background(Color.Green)
                .noRippleClickable { onPresent() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        // absent button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(32.dp)
                .background(Color.Red)
                .noRippleClickable { onAbsent() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        // cancelled button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(32.dp)
                .background(Color.Gray)
                .noRippleClickable { onCancelled() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "C",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}