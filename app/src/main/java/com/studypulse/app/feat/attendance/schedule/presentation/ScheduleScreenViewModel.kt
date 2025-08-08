package com.studypulse.app.feat.attendance.schedule.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScheduleScreenViewModel(
    private val periodRepository: PeriodRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialData = ScheduleScreenState(
        currentDay = Day.MONDAY,
        schedule = emptyList(),
        courseId = null,
        courseCode = null,
        showDeleteDialog = false,
        periodIdToDelete = null
    )
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    private val courseId: String? = savedStateHandle["courseId"]
    private val courseCode: String? = savedStateHandle["courseCode"]

    init {
        updateCourseIdAndCode(courseId)
        loadSchedule()
    }

    private fun updateCourseIdAndCode(courseId: String?) {
        _state.update {
            it.copy(courseId = courseId, courseCode = courseCode)
        }
    }

    fun toggleDay(newSelection: Day) {
        _state.update {
            it.copy(currentDay = newSelection)
        }

        loadSchedule()
    }

    fun updateShowDeleteDialog(show: Boolean) {
        _state.update {
            it.copy(showDeleteDialog = show)
        }
    }

    fun updatePeriodIdToDelete(periodId: String?) {
        _state.update {
            it.copy(periodIdToDelete = periodId)
        }
    }

    fun deletePeriod(periodId: String) {
        viewModelScope.launch {
            periodRepository.deletePeriod(periodId)
                .onSuccess {
                    SnackbarController.sendEvent(SnackbarEvent(message = "Period deleted successfully"))
                    loadSchedule()
                }
                .onFailure {
                    Log.d("tag", "${it.message}")
                    SnackbarController.sendEvent(SnackbarEvent(message = "Failed to delete period ${it.message}"))
                }
        }
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            if (courseId != null) {
                periodRepository.getAllPeriodsForCourseByDayInStartTimeOrder(
                    courseId,
                    _state.value.currentDay
                )
                    .onFailure { SnackbarController.sendEvent(SnackbarEvent(message = "Failed to load schedule")) }
                    .onSuccess { periodsFlow ->
                        periodsFlow.collect { periods ->
                            Log.d("fcuk", "Received $periods")
                            _state.update {
                                it.copy(schedule = periods)
                            }
                        }
                    }
            } else {
                periodRepository.getAllPeriodsByDayInStartTimeOrder(_state.value.currentDay)
                    .onFailure { SnackbarController.sendEvent(SnackbarEvent(message = "Failed to load schedule")) }
                    .onSuccess { periodsFlow ->
                        periodsFlow.collect { periods ->
                            _state.update {
                                it.copy(
                                    schedule = periods
                                )
                            }
                        }
                    }
            }
        }
    }
}