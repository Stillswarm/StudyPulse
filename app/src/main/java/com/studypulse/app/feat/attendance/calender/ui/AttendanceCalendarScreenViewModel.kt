package com.studypulse.app.feat.attendance.calender.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.semester.domain.SemesterRepository
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
    private val semesterRepository: SemesterRepository,
    private val periodRepository: PeriodRepository,
) : ViewModel() {
    private val initialData = AttendanceCalendarScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val sem = semesterRepository.getActiveSemester().getOrNull()
            if (sem == null) {
                SnackbarController.sendEvent(SnackbarEvent("Can't find an active semester"))
                return@launch
            }
            _state.update {
                it.copy(
                    semesterStartDate = sem.startDate,
                    sem.endDate
                )
            }
        }
    }

    fun onDateSelected(newDate: LocalDate?) {
        if (newDate == null) return

        // if outside semester range
        if (newDate.isBefore(_state.value.semesterStartDate) || newDate.isAfter(_state.value.semesterEndDate)) {
            _state.update {
                it.copy(
                    selectedDate = newDate,
                    periodsList = emptyList(),
                )
            }
        } else {
            viewModelScope.launch {
                periodRepository.getAllPeriodsFilteredByDayOfWeek(
                    Day.valueOf(newDate.dayOfWeek.name.uppercase())
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
                                            attendanceRecord =
                                                attendanceRepository.getAttendanceForPeriodAndDate(
                                                    period.id,
                                                    newDate
                                                )
                                        )
                                    }
                                )
                            }
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

    fun onDayCancelled() {
        if (_state.value.selectedDate == null || _state.value.selectedDate!!.isAfter(LocalDate.now())) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent(message = "Can't mark attendance for future dates"))
            }
        }
        _state.value.periodsList.forEach { periodWithAttendance ->
            Log.d(
                "tag",
                periodWithAttendance.period.courseName + " " + periodWithAttendance.attendanceRecord?.id
            )
            markAttendance(periodWithAttendance, AttendanceStatus.CANCELLED)
        }
    }

    fun updateShowBottomSheet(show: Boolean) {
        _state.update { it.copy(showBottomSheet = show) }
    }

    fun markAttendance(periodWithAttendance: PeriodWithAttendance, status: AttendanceStatus) {
        if (_state.value.selectedDate == null || _state.value.selectedDate!!.isAfter(LocalDate.now())) {
            _state.update { it.copy(showBottomSheet = false) }
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
                Log.d("tag", "marking ${record.id}")
                attendanceRepository.upsertAttendance(record)
            }
        }
    }
}