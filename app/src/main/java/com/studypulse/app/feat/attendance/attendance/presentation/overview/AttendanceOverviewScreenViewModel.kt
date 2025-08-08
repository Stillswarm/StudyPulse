package com.studypulse.app.feat.attendance.attendance.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.feat.attendance.attendance.domain.use_cases.GetCourseWiseSummariesUseCase
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AttendanceOverviewScreenViewModel(
    semesterSummaryRepository: SemesterSummaryRepository,
    getCourseWiseSummariesUseCase: GetCourseWiseSummariesUseCase,
) : ViewModel() {
    private val initialData = AttendanceOverviewScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    val courseWiseSummaryFlow = getCourseWiseSummariesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
    val semesterSummaryFlow = semesterSummaryRepository.getSummaryFlow().distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    init {
        viewModelScope.launch {
            combine(courseWiseSummaryFlow, semesterSummaryFlow) { c, s ->
                if (c.isNotEmpty() && s != null) {
                    _state.update { it.copy(loading = false) }
                }
            }.collect()
        }
    }
}