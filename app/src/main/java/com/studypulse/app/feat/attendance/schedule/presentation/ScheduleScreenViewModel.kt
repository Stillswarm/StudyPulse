package com.studypulse.app.feat.attendance.schedule.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.schedule.data.Day
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
    )
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    private val courseId: Long? = savedStateHandle["courseId"]

    init {
        updateCourseId(courseId)
        loadSchedule()
    }

    private fun updateCourseId(courseId: Long?) {
        _state.update {
            it.copy(courseId = courseId)
        }
    }

    fun toggleDay(newSelection: Day) {
        _state.update {
            it.copy(currentDay = newSelection)
        }

        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            if (courseId != null) {
                periodRepository.getAllPeriodsForCourseFilteredByDayOfWeek(
                    courseId,
                    _state.value.currentDay
                )
                    .onFailure { SnackbarController.sendEvent(SnackbarEvent(message = "Failed to load schedule")) }
                    .onSuccess { periodsFlow ->
                        periodsFlow.collect { periods ->
                            _state.update {
                                it.copy(schedule = periods)
                            }
                        }
                    }
            } else {
                periodRepository.getAllPeriodsFilteredByDayOfWeek(_state.value.currentDay)
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