package com.studypulse.app.feat.attendance.calender.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.schedule.data.Day
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class AttendanceCalendarScreenViewModel(
    private val periodRepository: PeriodRepository,
) : ViewModel() {
    private val initialData = AttendanceCalendarScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    fun onDateSelected(newDate: LocalDate?) {
        viewModelScope.launch {
            periodRepository.getAllPeriodsFilteredByDayOfWeek(
                Day.valueOf(newDate?.dayOfWeek?.name?.uppercase() ?: "MONDAY")
            )
                .onFailure { SnackbarController.sendEvent(SnackbarEvent(message = "couldn't fetch schedule")) }
                .onSuccess { periodFlow ->
                    periodFlow.flowOn(Dispatchers.IO).collect { periods ->
                        _state.update {
                            it.copy(
                                selectedDate = newDate,
                                periodsList = periods
                            )
                        }
                    }
                }
        }
        _state.update {
            it.copy(selectedDate = newDate)
        }
    }

    fun onMonthChanged(newYearMonth: YearMonth) {
        _state.update { it.copy(yearMonth = newYearMonth) }
    }

    fun clearSelectedDate() {
        _state.update { it.copy(selectedDate = null) }
    }
}