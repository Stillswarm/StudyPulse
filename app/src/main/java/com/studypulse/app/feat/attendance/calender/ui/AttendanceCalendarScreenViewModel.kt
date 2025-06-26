package com.studypulse.app.feat.attendance.calender.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class AttendanceCalendarScreenViewModel(
    private val attendanceRepository: AttendanceRepository,
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
                                periodsList = periods.map { period ->
                                    PeriodWithAttendance(
                                        period = period,
                                        attendanceRecord = newDate?.let { it1 ->
                                            attendanceRepository.getAttendanceForPeriodAndDate(period.id, it1)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
        }
    }

    fun onMonthChanged(newYearMonth: YearMonth) {
        _state.update { it.copy(yearMonth = newYearMonth) }
    }

    fun clearSelectedDate() {
        _state.update { it.copy(selectedDate = null) }
    }

    fun markAttendance(periodWithAttendance: PeriodWithAttendance, status: AttendanceStatus) {
        if (_state.value.selectedDate == null || _state.value.selectedDate!!.isAfter(LocalDate.now())) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent(message = "Can't mark attendance for future dates"))
            }
        } else {
            val record =
                periodWithAttendance.attendanceRecord?.copy(status = status) ?: AttendanceRecord(
                    periodId = periodWithAttendance.period.id,
                    date = _state.value.selectedDate!!,
                    status = status,
                    courseId = periodWithAttendance.period.courseId
                )
            viewModelScope.launch {
                attendanceRepository.upsertAttendance(record)
            }
        }
    }
}