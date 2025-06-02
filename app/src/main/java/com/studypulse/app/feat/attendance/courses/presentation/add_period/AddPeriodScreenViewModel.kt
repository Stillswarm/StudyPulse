package com.studypulse.app.feat.attendance.courses.presentation.add_period

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.schedule.data.Day
import com.studypulse.app.feat.attendance.schedule.data.Period
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class AddPeriodScreenViewModel(
    private val courseRepository: CourseRepository,
    private val periodRepository: PeriodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val initialData = AddPeriodState(
        selectedDay = Day.MONDAY,
        startTimeHour = 9,
        startTimeMinute = 0,
        endTimeMinute = 0,
        endTimeHour = 10
    )
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    private val courseId: Long = checkNotNull(savedStateHandle["courseId"])

    fun onDayChange(newVal: Day) {
        _state.update { it.copy(selectedDay = newVal) }
    }

    fun onStartTimeChange(newHour: Int, newMinute: Int) {
        _state.update { it.copy(startTimeHour = newHour, startTimeMinute = newMinute) }
    }

    fun onEndTimeChange(newHour: Int, newMinute: Int) {
        _state.update { it.copy(endTimeHour = newHour, endTimeMinute = newMinute) }
    }

    fun onSubmit() {
        viewModelScope.launch {
            periodRepository.addNewPeriod(
                Period(
                    courseId = courseId,
                    courseName = courseRepository.getCourseById(courseId).getOrNull()?.courseName ?: "",
                    day = _state.value.selectedDay,
                    startTime = LocalTime.of(_state.value.startTimeHour, _state.value.startTimeMinute),
                    endTime = LocalTime.of(_state.value.endTimeHour, _state.value.endTimeMinute),
                )
            )
        }
    }
}