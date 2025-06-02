package com.studypulse.app.feat.attendance.calender.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.feat.attendance.calender.ui.components.AttendanceCalendar
import com.studypulse.app.feat.attendance.calender.ui.components.DayCoursesBottomSheetContent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceCalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: AttendanceCalendarScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Attendance Calendar",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(8.dp))

        val dayCoursesBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        val scope = rememberCoroutineScope()

        DisposableEffect(Unit) {
            onDispose {
                scope.launch {
                    dayCoursesBottomSheetState.hide()
                }
            }
        }

        state.selectedDate?.let {
            if (dayCoursesBottomSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        scope.launch {
                            viewModel.clearSelectedDate()
                            dayCoursesBottomSheetState.hide()
                        }
                    },
                    sheetState = dayCoursesBottomSheetState,
                    shape = RectangleShape,
                    dragHandle = {}
                ) {
                    DayCoursesBottomSheetContent(
                        periodList = state.periodsList,
                        localDate = state.selectedDate!!,
                        onClose = {
                            scope.launch {
                                viewModel.clearSelectedDate()
                                dayCoursesBottomSheetState.hide()
                            }
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
                scope.launch { dayCoursesBottomSheetState.show() }
            },
            onMonthChanged = { viewModel.onMonthChanged(it) }
        )
    }
}