package com.studypulse.app.feat.attendance.calender.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecordDto
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.domain.model.toDomain
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
                    semesterEndDate = sem.endDate,
                    semesterId = sem.id
                )
            }
        }
        onMonthChanged(_state.value.yearMonth)
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
                try {
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
                                                    ) ?: AttendanceRecordDto().toDomain()
                                            )
                                        }
                                    )
                                }
                            }
                        }
                } catch (e: Exception) {
                    if (e is kotlinx.coroutines.CancellationException) throw e
                    SnackbarController.sendEvent(SnackbarEvent("Error fetching schedule " + e.localizedMessage))
                    Log.d("tag", e.message ?: "unknown")
                }
            }
        }
    }

    fun onMonthChanged(newYearMonth: YearMonth) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    yearMonth = newYearMonth,
                    unmarkedDates = attendanceRepository.getDatesWithUnmarkedAttendance(
                        it.semesterId,
                        newYearMonth.atDay(1),
                        LocalDate.now()
                    ).getOrNull() ?: emptySet()
                )
            }

        }
    }

    fun clearSelectedDate() {
        _state.update { it.copy(selectedDate = null) }
    }

    fun onDayCancelled() {
        val selectedDate = _state.value.selectedDate
        if (selectedDate == null || selectedDate.isAfter(LocalDate.now())) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent(message = "Can't mark attendance for future dates"))
            }
            return
        }

        viewModelScope.launch {
            try {
                val periodsForDay = _state.value.periodsList
                if (periodsForDay.isEmpty()) return@launch
                periodsForDay.map { pwa ->
                    pwa.attendanceRecord.copy(status = AttendanceStatus.CANCELLED)
                }.map { launch { attendanceRepository.upsertAttendance(it) } }

            } catch (e: Exception) {
                SnackbarController.sendEvent(SnackbarEvent(message = "Error marking attendance: ${e.message}"))
            }
        }

        _state.update {
            val new = it.unmarkedDates - selectedDate
            it.copy(unmarkedDates = new)
        }
    }

    /**
     * Checks if the given date still has any pending attendance records and updates
     * the `datesWithPendingAttendance` set in the state accordingly.
     */
    private fun checkAndUpdatePendingStatusForDate(date: LocalDate) {
        viewModelScope.launch {
            // This repository function needs to check if *any* record for this date is PENDING
            val hasPending =
                attendanceRepository.hasPendingAttendanceForDate(_state.value.semesterId, date)
                    .getOrDefault(false)

            _state.update { currentState ->
                val updatedPendingDates = if (hasPending) {
                    currentState.unmarkedDates + date
                } else {
                    currentState.unmarkedDates - date
                }
                currentState.copy(unmarkedDates = updatedPendingDates)
            }
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
                periodWithAttendance.attendanceRecord.copy(status = status)
            viewModelScope.launch {
                Log.d("tag", "marking ${record.id}")
                attendanceRepository.upsertAttendance(record)
            }
        }
    }
}