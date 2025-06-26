package com.studypulse.app.feat.attendance.attendance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AttendanceStatsSharedViewModel(
    private val courseRepository: CourseRepository,
    attendanceRepository: AttendanceRepository
) : ViewModel() {

    val attendanceByCourse = attendanceRepository.getAttendanceGroupedByCourse()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val allCoursesMap: StateFlow<Map<String, Course>> = flow {
        val result = courseRepository.getAllCourses()
        if (result.isSuccess) {
            emitAll(
                result.getOrNull()!!.map { courses ->
                    courses.associateBy { it.id }
                }
            )
        } else {
            emit(emptyMap())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )
}