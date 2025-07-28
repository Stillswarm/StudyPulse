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
    private val initialData = AddPeriodScreenState(
        selectedDay = savedStateHandle.get<Day>("day") ?: Day.MONDAY,
        startTimeHour = 9,
        startTimeMinute = 0,
        endTimeMinute = 0,
        endTimeHour = 10
    )
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    private val courseId: String = checkNotNull(savedStateHandle["courseId"])
    private val periodId: String? = savedStateHandle["periodId"]

    init {
        if (periodId != null) {
            viewModelScope.launch {
                val period = periodRepository.getPeriodById(periodId).getOrNull()
                if (period != null) {
                    _state.update {
                        it.copy(
                            periodId = periodId,
                            selectedDay = period.day,
                            startTimeHour = period.startTime.hour,
                            startTimeMinute = period.startTime.minute,
                            endTimeHour = period.endTime.hour,
                            endTimeMinute = period.endTime.minute
                        )
                    }
                }
            }
        }
    }

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

    fun updateShowConfirmationPopup(new: Boolean) {
        _state.update { it.copy(showConfirmationPopup = new) }
    }

    fun updateGranted(new: Boolean) {
        _state.update { it.copy(granted = new) }
    }

    fun onSubmit(navigateBack: () -> Unit) {
        if (_state.value.startTimeHour > _state.value.endTimeHour) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent("start time cannot be after end time"))
            }
            return
        }

        // check for abnormal date range
        val s = _state.value
        val startTime = LocalTime.of(s.startTimeHour, s.startTimeMinute)
        val endTime = LocalTime.of(s.endTimeHour, s.endTimeMinute)
        val durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes()
        if (!s.granted) {
            if (durationMinutes < 30) {
                _state.update {
                    it.copy(
                        showConfirmationPopup = true,
                        timeRange = "$durationMinutes minutes"
                    )
                }
                return
            } else if (durationMinutes > 180) {
                _state.update {
                    it.copy(
                        showConfirmationPopup = true,
                        timeRange = "${durationMinutes / 60} hours ${durationMinutes % 60} minutes"
                    )
                }
                return
            }
        }


        viewModelScope.launch {
            periodRepository.addNewPeriod(
                Period(
                    id = _state.value.periodId, // if new period -> periodId = some valid id, else periodId = "", rest of the logic is in addNewPeriod()
                    courseId = courseId,
                    courseName = courseRepository.getCourseById(courseId)
                        .getOrNull()?.courseName
                        ?: "",
                    day = _state.value.selectedDay,
                    startTime = startTime,
                    endTime = endTime,
                    semesterId = semesterRepository.getActiveSemester().getOrNull()?.id ?: ""
                )
            )

            _state.update {
                it.copy(
                    showConfirmationPopup = false,
                    granted = false,
                    timeRange = "",
                )
            }
            navigateBack()
        }
    }
}