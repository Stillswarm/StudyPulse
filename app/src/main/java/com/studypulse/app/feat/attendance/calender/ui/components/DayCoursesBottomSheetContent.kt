package com.studypulse.app.feat.attendance.calender.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.common.util.toFullString
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.calender.ui.PeriodWithAttendance
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.GreenNormal
import com.studypulse.app.ui.theme.LightGray
import com.studypulse.app.ui.theme.Red
import com.studypulse.app.ui.theme.WarmWhite
import com.studypulse.app.ui.theme.WhiteSecondary
import java.time.LocalDate

@Composable
fun DayCoursesBottomSheetContent(
    periodList: List<PeriodWithAttendance>,
    localDate: LocalDate,
    buttonEnabled: Boolean,
    onCancelDay: () -> Unit,
    onClose: () -> Unit,
    onPresent: (PeriodWithAttendance) -> Unit,
    onAbsent: (PeriodWithAttendance) -> Unit,
    onCancelled: (PeriodWithAttendance) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dayCancelled by remember { mutableStateOf(false) }
    Box(modifier = modifier
        .fillMaxSize()
        .background(WhiteSecondary)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Space for button
        ) {

            item {
                Spacer(modifier = Modifier.height(24.dp))

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
                            color = DarkGray
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "hide",
                        modifier = Modifier.noRippleClickable { onClose() }
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        dayCancelled = true
                        onCancelDay()
                    },
                    enabled = buttonEnabled,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold.copy(alpha = 0.2f),
                    ),
                    border = BorderStroke(2.dp, Gold.copy(alpha = if (buttonEnabled) 1f else 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Mark as Holiday/Cancelled",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.Black
                    )
                }
            }

            items(periodList) { periodWithAttendance ->
                BottomSheetItem(
                    period = periodWithAttendance.period,
                    attendanceRecord = periodWithAttendance.attendanceRecord,
                    onPresent = { onPresent(periodWithAttendance) },
                    onAbsent = { onAbsent(periodWithAttendance) },
                    onCancelled = { onCancelled(periodWithAttendance) },
                    dayCancelled = dayCancelled
                )
            }
        }
    }
}

@Composable
fun BottomSheetItem(
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onCancelled: () -> Unit,
    period: Period,
    dayCancelled: Boolean,
    attendanceRecord: AttendanceRecord?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WarmWhite)
            .border(1.dp, Gold, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                color = DarkGray,
                lineHeight = 20.sp
            )

            AttendanceStatusButtonsRow(
                attendanceRecord = attendanceRecord,
                onPresent = onPresent,
                onAbsent = onAbsent,
                onCancelled = onCancelled,
                dayCancelled = dayCancelled
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
    modifier: Modifier = Modifier,
    dayCancelled: Boolean = false,
) {
    Log.d("tag", "AttendanceStatusButtonsRow: $dayCancelled")
    var dayCancelled by remember {
        mutableStateOf(dayCancelled)
    }
    var presentColor by remember { mutableStateOf(if (attendanceRecord == null || attendanceRecord.status != AttendanceStatus.PRESENT) LightGray else GreenNormal) }
    var absentColor by remember { mutableStateOf(if (attendanceRecord == null || attendanceRecord.status != AttendanceStatus.ABSENT) LightGray else Red) }
    var cancelledColor by remember { mutableStateOf(if (attendanceRecord == null || attendanceRecord.status != AttendanceStatus.CANCELLED) LightGray else DarkGray) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // present button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(32.dp)
                .background(if (dayCancelled) DarkGray else presentColor)
                .noRippleClickable {
                    onPresent()
                    presentColor = GreenNormal
                    cancelledColor = LightGray
                    absentColor = LightGray
                    dayCancelled = false
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
                .background(if (dayCancelled) DarkGray else absentColor)
                .noRippleClickable {
                    onAbsent()
                    absentColor = Red
                    presentColor = LightGray
                    cancelledColor = LightGray
                    dayCancelled = false
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
                .background(if (dayCancelled) DarkGray else cancelledColor)
                .noRippleClickable {
                    onCancelled()
                    cancelledColor = DarkGray
                    presentColor = LightGray
                    dayCancelled = false
                    absentColor = LightGray
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