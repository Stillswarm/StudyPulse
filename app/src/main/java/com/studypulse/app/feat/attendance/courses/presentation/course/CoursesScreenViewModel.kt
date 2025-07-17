package com.studypulse.app.feat.attendance.courses.presentation.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CoursesScreenViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val initialData = CoursesScreenState(courses = emptyList(), isLoading = false)
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    init {
        loadCoursesData()
    }

    fun loadCoursesData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            courseRepository.getAllCoursesSortedByNameFlow()
                .onFailure {
                    SnackbarController.sendEvent(SnackbarEvent("Error occurred"))
                    _state.update { it.copy(isLoading = false) }
                }
                .onSuccess { courseFlow ->
                    courseFlow.flowOn(Dispatchers.IO).collect { courses ->
                        _state.update {
                            it.copy(courses = courses, isLoading = false)
                        }
                    }
                }
        }
    }
}