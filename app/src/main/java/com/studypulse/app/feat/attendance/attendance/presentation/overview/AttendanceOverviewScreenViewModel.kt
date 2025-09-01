package com.studypulse.app.feat.attendance.attendance.presentation.overview

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.common.util.MathUtils
import com.studypulse.app.feat.attendance.attendance.domain.use_cases.GetCourseWiseSummariesUseCase
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import com.studypulse.app.nav.OverviewType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AttendanceOverviewScreenViewModel(
    semesterSummaryRepository: SemesterSummaryRepository,
    getCourseWiseSummariesUseCase: GetCourseWiseSummariesUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val initialData = AttendanceOverviewScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val overviewTypeString = savedStateHandle.get<String>("overviewType")
    val overviewType =
        if (overviewTypeString != null) OverviewType.valueOf(overviewTypeString) else OverviewType.ALL

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
            _state.update { it.copy(loading = true) }
            combine(courseWiseSummaryFlow, semesterSummaryFlow) { c, s ->
                Log.d("AttendanceOverviewScreenViewModel", "combine: $overviewType")
                var updatedTopBarTitle = when (overviewType) {
                    OverviewType.ALL -> "Attendance Overview"
                    OverviewType.FULL -> "100% Attendance"
                    OverviewType.LOW -> "Low Attendance"
                }
                val updatedCourseMap = when (overviewType) {
                    OverviewType.ALL -> c
                    OverviewType.FULL -> c.filter {
                        MathUtils.calculatePercentage(
                            it.value.presentRecords,
                            it.value.totalClasses
                        ) == 100
                    }

                    OverviewType.LOW -> c.filter {
                        MathUtils.calculatePercentage(
                            it.value.presentRecords,
                            it.value.totalClasses
                        ) < it.value.minAttendance
                    }
                }
                _state.update {
                    it.copy(
                        courseWiseSummaries = updatedCourseMap,
                        topBarTitle = updatedTopBarTitle,
                        loading = false,
                    )
                }
            }.drop(1).collect()
        }
    }
}