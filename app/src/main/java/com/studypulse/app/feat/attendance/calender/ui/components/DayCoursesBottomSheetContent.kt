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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.common.ui.components.noRippleClickable
import com.studypulse.app.common.util.toFullString
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.calender.ui.PeriodWithAttendance
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import java.time.LocalDate

@Composable
fun DayCoursesBottomSheetContent(
    periodList: List<PeriodWithAttendance>,
    localDate: LocalDate,
    onClose: () -> Unit,
    onPresent: (PeriodWithAttendance) -> Unit,
    onAbsent: (PeriodWithAttendance) -> Unit,
    onCancelled: (PeriodWithAttendance) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
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

        periodList.forEach { periodWithAttendance ->
            BottomSheetItem(
                period = periodWithAttendance.period,
                attendanceRecord = periodWithAttendance.attendanceRecord,
                onPresent = { onPresent(periodWithAttendance) },
                onAbsent = { onAbsent(periodWithAttendance) },
                onCancelled = { onCancelled(periodWithAttendance) }
            )
        }
    }
}

@Composable
fun BottomSheetItem(
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onCancelled: () -> Unit,
    period: Period,
    attendanceRecord: AttendanceRecord?,
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
                attendanceRecord = attendanceRecord,
                onPresent = onPresent,
                onAbsent = onAbsent,
                onCancelled = onCancelled
            )
        }
    }
}

@Composable
fun AttendanceStatusButtonsRow(
    attendanceRecord: AttendanceRecord?,
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onCancelled: () -> Unit,
) {
    var presentColor by remember { mutableStateOf(if (attendanceRecord == null || attendanceRecord.status != AttendanceStatus.PRESENT) Color.Gray else Color.Green) }
    var absentColor by remember { mutableStateOf(if (attendanceRecord == null || attendanceRecord.status != AttendanceStatus.ABSENT) Color.Gray else Color.Red) }
    var cancelledColor by remember { mutableStateOf(if (attendanceRecord == null || attendanceRecord.status != AttendanceStatus.CANCELLED) Color.Gray else Color.DarkGray) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // present button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(32.dp)
                .background(presentColor)
                .noRippleClickable {
                    onPresent()
                    presentColor = Color.Green
                    cancelledColor = Color.Gray
                    absentColor = Color.Gray
                },
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
                .background(absentColor)
                .noRippleClickable {
                    onAbsent()
                    absentColor = Color.Red
                    presentColor = Color.Gray
                    cancelledColor = Color.Gray
                },
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
                .background(cancelledColor)
                .noRippleClickable {
                    onCancelled()
                    cancelledColor = Color.DarkGray
                    presentColor = Color.Gray
                    absentColor = Color.Gray
                },
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