package com.studypulse.app.feat.attendance.courses.presentation.add_period

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.feat.semester.domain.SemesterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class AddPeriodScreenViewModel(
    private val courseRepository: CourseRepository,
    private val periodRepository: PeriodRepository,
    private val semesterRepository: SemesterRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val initialData = AddPeriodState(
        selectedDay = savedStateHandle.get<Day>("day") ?: Day.MONDAY,
        startTimeHour = 9,
        startTimeMinute = 0,
        endTimeMinute = 0,
        endTimeHour = 10
    )
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    fun showStartTimePicker() {
        _state.update {
            it.copy(
                showStartTimePicker = true,
                showEndTimePicker = false
            )
        }
    }

    fun hideStartTimePicker() {
        _state.update { it.copy(showStartTimePicker = false) }
    }

    fun showEndTimePicker() {
        _state.update {
            it.copy(
                showEndTimePicker = true,
                showStartTimePicker = false
            )
        }
    }

    fun hideEndTimePicker() {
        _state.update { it.copy(showEndTimePicker = false) }
    }


    fun onDayChange(newVal: Day) {
        _state.update { it.copy(selectedDay = newVal) }
    }

    fun onStartTimeChange(newHour: Int, newMinute: Int) {
        _state.update { it.copy(startTimeHour = newHour, startTimeMinute = newMinute) }
    }

    fun onEndTimeChange(newHour: Int, newMinute: Int) {
        _state.update { it.copy(endTimeHour = newHour, endTimeMinute = newMinute) }
    }

    fun onSubmit(navigateBack: () -> Unit) {
        if (_state.value.startTimeHour > _state.value.endTimeHour) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent("start time cannot be after end time"))
            }
            return
        }
        viewModelScope.launch {
            periodRepository.addNewPeriod(
                Period(
                    courseId = courseId,
                    courseName = courseRepository.getCourseById(courseId).getOrNull()?.courseName
                        ?: "",
                    day = _state.value.selectedDay,
                    startTime = LocalTime.of(
                        _state.value.startTimeHour,
                        _state.value.startTimeMinute
                    ),
                    endTime = LocalTime.of(_state.value.endTimeHour, _state.value.endTimeMinute),
                    semesterId = semesterRepository.getActiveSemester().getOrNull()?.id ?: ""

                )
            )

            navigateBack()
        }
    }
}