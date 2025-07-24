package com.studypulse.app.feat.attendance.calender.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.calender.ui.components.AttendanceCalendar
import com.studypulse.app.feat.attendance.calender.ui.components.DayCoursesBottomSheetContent
import com.studypulse.app.ui.theme.Gold
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
                        onCancelDay = { viewModel.onDayCancelled() }
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
    }
}