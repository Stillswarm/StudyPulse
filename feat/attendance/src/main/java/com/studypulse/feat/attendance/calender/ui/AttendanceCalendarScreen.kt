package com.studypulse.feat.attendance.calender.ui

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.common.utils.DateUtils.to12HourString
import com.studypulse.common.utils.DateUtils.toStandardString
import com.studypulse.feat.attendance.R
import com.studypulse.ui.components.AppTopBar
import com.studypulse.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.feat.attendance.calender.ui.components.AttendanceCalendar
import com.studypulse.feat.attendance.calender.ui.components.AttendanceStatusButtonsRow
import com.studypulse.feat.attendance.calender.ui.components.DayCoursesBottomSheetContent
import com.studypulse.ui.theme.Gold
import com.studypulse.ui.theme.WarmWhite
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceCalendarScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AttendanceCalendarScreenViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            backgroundColor = Gold,
            foregroundGradient = null,
            title = "Attendance Calendar",
            navigationIcon = R.drawable.ic_arrow_left,
            onNavigationClick = { navigateUp() },
            actionIcon = null,
            onActionClick = null,
        )

        Spacer(Modifier.height(8.dp))

        DisposableEffect(Unit) {
            onDispose {
                viewModel.updateShowBottomSheet(false)
            }
        }

        val selectedDate = state.selectedDate
        if (state.showBottomSheet && selectedDate != null) {
            val dayCoursesBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.updateShowBottomSheet(false)
                    viewModel.clearSelectedDate()
                },
                sheetState = dayCoursesBottomSheetState,
                shape = RectangleShape,
                dragHandle = {}
            ) {
                DayCoursesBottomSheetContent(
                    periodList = state.periodsList,
                    localDate = selectedDate,
                    buttonEnabled = state.periodsList.isNotEmpty(),
                    onClose = {
                        viewModel.updateShowBottomSheet(false)
                    },
                    onPresent = {
                        viewModel.markAttendance(it, AttendanceStatus.PRESENT)
                    },
                    onAbsent = {
                        viewModel.markAttendance(it, AttendanceStatus.ABSENT)
                    },
                    onCancelled = {
                        viewModel.markAttendance(it, AttendanceStatus.CANCELLED)
                    },
                    onCancelDay = {
                        viewModel.onDayCancelled()
                        viewModel.updateShowBottomSheet(false)
                    }
                )
            }
        }

        AttendanceCalendar(
            yearMonth = state.yearMonth,
            selectedDate = state.selectedDate,
            eventDates = emptySet(),
            onDateSelected = {
                viewModel.onDateSelected(it)
                viewModel.updateShowBottomSheet(true)
            },
            onMonthChanged = { viewModel.onMonthChanged(it) },
            unmarkedPeriods = state.unmarkedDates
        )

        val recordsToMark by viewModel.recordsToMark.collectAsStateWithLifecycle()

        if (recordsToMark.isNotEmpty()) {
            Text(
                text = "Quick Attendance",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(16.dp)
            )

            Log.d("tag", recordsToMark.size.toString())
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = recordsToMark,
                    key = { it.attendanceRecord.id },
                ) { pwa ->
                    QuickAttendanceBox(
                        pwa = pwa,
                        onMarkAttendance = { viewModel.markAttendance(pwa, it) },
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(durationMillis = 1000),
                            placementSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAttendanceBox(
    pwa: PeriodWithAttendance,
    onMarkAttendance: (AttendanceStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .sizeIn(maxWidth = 200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(WarmWhite)
            .border(1.dp, Gold, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
//            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = pwa.period.courseName,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = pwa.attendanceRecord.date.toStandardString(),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${pwa.period.startTime.to12HourString()} - ${pwa.period.endTime.to12HourString()}",
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            AttendanceStatusButtonsRow(
                attendanceRecord = null,
                onPresent = { onMarkAttendance(AttendanceStatus.PRESENT) },
                onAbsent = { onMarkAttendance(AttendanceStatus.ABSENT) },
                onCancelled = { onMarkAttendance(AttendanceStatus.CANCELLED) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
