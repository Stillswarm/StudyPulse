package com.studypulse.app.feat.attendance.courses.presentation.add_course

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.model.Semester
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddCourseScreenViewModel(
    private val courseRepository: CourseRepository,
    private val semesterRepository: SemesterRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var courseId: String? = null
    private val initialData = AddCourseScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    init {
        courseId = savedStateHandle.get<String?>("courseId")
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            launch { loadCourseDetails() }
            launch { loadAllSemesters() }
        }
        _state.update { it.copy(isLoading = false) }
    }

    suspend fun loadCourseDetails() {
        Log.d("tag", "loadCourseDetails: $courseId")
        courseId?.let { cid ->
            courseRepository.getCourseById(cid)
                .onSuccess { course ->
                    if (course == null) return@let
                    Log.d("tag", "loadCourseDetails: $course")
                    _state.update {
                        it.copy(
                            courseName = course.courseName,
                            courseCode = course.courseCode,
                            instructor = course.instructor,
                            minAttendance = course.minAttendance.toString()
                        )
                    }
                }.onFailure {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = it.message ?: "Unknown error"
                        )
                    )
                }
        }
    }

    suspend fun loadAllSemesters() {
        semesterRepository.getAllSemesters()
            .onFailure { e ->
                _state.update { it.copy(errorMsg = e.message) }
            }
            .onSuccess { a ->
                _state.update {
                    it.copy(
                        allSemesters = a,
                        activeSemester = a.first { it.isCurrent })
                }
            }
    }

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

    fun updateMinAttendance(new: String) {
        _state.update { it.copy(minAttendance = new, errorMsg = null) }
    }

    fun onSubmit(onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            if (state.value.courseName.isBlank() || state.value.courseCode.isBlank() || state.value.minAttendance.isBlank())
                SnackbarController.sendEvent(
                    SnackbarEvent(message = "Fields cannot be empty")
                )
            else {
                courseRepository.upsertCourse(
                    Course(
                        id = courseId ?: "",
                        courseName = _state.value.courseName,
                        courseCode = _state.value.courseCode,
                        instructor = _state.value.instructor,
                        semesterId = _state.value.activeSemester?.id ?: "",
                        minAttendance = _state.value.minAttendance.toInt()
                    )
                )
                onNavigateBack()
            }
        }
    }

    private fun loadCurrentSemester() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            semesterRepository.getActiveSemester()
                .onFailure { e ->
                    _state.update { it.copy(errorMsg = e.message) }
                }
                .onSuccess { a ->
                    if (a == null) _state.update { it.copy(errorMsg = "No active semester.") }
                    _state.update { it.copy(activeSemester = a) }
                }
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun updateCurrentSemester(new: Semester) {
        if (new == _state.value.activeSemester) return
        viewModelScope.launch {
            if (new.endDate > LocalDate.now()) {
                SnackbarController.sendEvent(SnackbarEvent("Active semester cannot be in the past"))
            } else {
                semesterRepository.markCurrent(new.id)
                    .onSuccess { _state.update { it.copy(activeSemester = new) } }
            }
        }
    }
}