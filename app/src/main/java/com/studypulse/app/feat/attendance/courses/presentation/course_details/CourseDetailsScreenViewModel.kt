package com.studypulse.app.feat.attendance.courses.presentation.course_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourseDetailsScreenViewModel(
    private val courseRepository: CourseRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialData = CourseDetailsState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val courseId = savedStateHandle.get<Long>("courseId") ?: 0L

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    course = courseRepository.getCourseById(courseId).getOrNull()
                )
            }

            _state.update { it.copy(isLoading = false) }
        }
    }
}