package com.studypulse.app.feat.attendance.calender.ui

import android.R.attr.fontWeight
import android.R.attr.text
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.util.to12HourString
import com.studypulse.app.common.util.toStandardString
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.presentation.home.QuickAttendanceBox
import com.studypulse.app.feat.attendance.calender.ui.components.AttendanceCalendar
import com.studypulse.app.feat.attendance.calender.ui.components.AttendanceStatusButtonsRow
import com.studypulse.app.feat.attendance.calender.ui.components.DayCoursesBottomSheetContent
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.WarmWhite
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceCalendarScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AttendanceCalendarScreenViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val dayCoursesBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    LaunchedEffect(state.showBottomSheet) {
        if (state.showBottomSheet) dayCoursesBottomSheetState.show()
        else {
            viewModel.clearSelectedDate()
            dayCoursesBottomSheetState.hide()
        }
    }

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

//        val scope = rememberCoroutineScope()
//        val hideSheetAndClearDate = remember {
//            {
//                scope.launch {
//                    viewModel.clearSelectedDate()
////                    dayCoursesBottomSheetState.hide()
//                    viewModel.updateShowBottomSheet(false)
//                }
//            }
//        }

        DisposableEffect(Unit) {
            onDispose {
//                scope.launch {
                viewModel.updateShowBottomSheet(false)
//                    dayCoursesBottomSheetState.hide()
            }
//            }
        }

        state.selectedDate?.let {
            if (state.showBottomSheet && dayCoursesBottomSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.updateShowBottomSheet(false) },
                    sheetState = dayCoursesBottomSheetState,
                    shape = RectangleShape,
                    dragHandle = {}
                ) {
                    DayCoursesBottomSheetContent(
                        periodList = state.periodsList,
                        localDate = state.selectedDate!!,
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
        }

        AttendanceCalendar(
            yearMonth = state.yearMonth,
            selectedDate = state.selectedDate,
            eventDates = emptySet(),
            onDateSelected = {
                viewModel.onDateSelected(it)
                viewModel.updateShowBottomSheet(true)
//                scope.launch { dayCoursesBottomSheetState.show() }
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
