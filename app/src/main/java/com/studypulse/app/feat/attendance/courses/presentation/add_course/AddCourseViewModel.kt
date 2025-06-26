package com.studypulse.app.feat.attendance.courses.presentation.add_course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCourseViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {
    private val initialData = AddCourseScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    fun onCourseNameChange(newVal: String) {
        _state.update {
            it.copy(courseName = newVal)
        }
    }

    fun onCourseCodeChange(newVal: String) {
        _state.update {
            it.copy(courseCode = newVal)
        }
    }

    fun onInstructorChange(newVal: String) {
        _state.update {
            it.copy(instructor = newVal)
        }
    }

    fun onSubmit(onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            if (state.value.courseName.isBlank() || state.value.courseCode.isBlank())
                SnackbarController.sendEvent(
                    SnackbarEvent(message = "Fields cannot be empty")
                )
            else {
                courseRepository.addCourse(
                    Course(
                        courseName = _state.value.courseName,
                        courseCode = _state.value.courseCode,
                        instructor = _state.value.instructor,
                    )
                )
                onNavigateBack()
            }
        }
    }
}