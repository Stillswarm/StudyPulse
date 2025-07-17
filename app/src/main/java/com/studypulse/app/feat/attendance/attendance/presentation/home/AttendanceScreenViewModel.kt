package com.studypulse.app.feat.attendance.attendance.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import com.studypulse.app.feat.semester.domain.model.SemesterSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class AttendanceScreenViewModel(
    private val semesterSummaryRepository: SemesterSummaryRepository,
    private val courseSummaryRepository: CourseSummaryRepository,
    ds: AppDatastore,
) : ViewModel() {
    private val initialData = AttendanceScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    val semesterIdFlow = ds.semesterIdFlow.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000, 0),
        initialValue = ""
    )

    init {
        fetchStatBoxData()
    }

    fun fetchStatBoxData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
//            delay(1_000)
            if (semesterIdFlow.value != "") {
                supervisorScope {
                    Log.d("tag", "fetching stat box data")
                    val semDataDef = async { semesterSummaryRepository.get() }
                    val courseDataDef = async { courseSummaryRepository.getSummaryForAllCourses() }

                    val courseData = courseDataDef.await().getOrElse { error ->
                        Log.d("tag", error.message ?: "unknown error in course")
                        SnackbarController.sendEvent(
                            SnackbarEvent("Failed to fetch courses summary: ${error.message ?: "Unknown error"}")
                        )
                        _state.update { it.copy(isLoading = false) }
                        return@supervisorScope
                    }
                    val semData = semDataDef.await().getOrElse { error ->
                        Log.d("tag", error.message ?: "unknown error in sem")
                        SnackbarController.sendEvent(
                            SnackbarEvent("Failed to fetch semester summary: ${error.message ?: "Unknown error"}")
                        )
                        _state.update { it.copy(isLoading = false) }
                        return@supervisorScope
                    }
                    Log.d("tag", "course data: $courseData")
                    _state.update {
                        it.copy(
                            unmarkedCount = semData.unmarkedRecords,
                            courseWiseSummaries = courseData,
                            fullAttendanceCount = courseData.filter { entry -> getPercent(entry) == 100 }.size,
                            lowAttendanceCount = courseData.filter { entry -> getPercent(entry) < entry.minAttendance }.size,
                            attendancePercentage = getPercentForSem(semData),
                            isLoading = false
                        )
                    }
                    Log.d("tag", "have now: " + state.value.courseWiseSummaries.toString())
                }
            } else _state.update { it.copy(isLoading = false) }
        }
    }

    fun getPercent(courseSummary: CourseSummary): Int {
        val total = courseSummary.presentRecords + courseSummary.absentRecords + courseSummary.unmarkedRecords
        Log.d("tag", "total: $total, present: ${courseSummary.presentRecords}")
        return if (total == 0) 0 else (courseSummary.presentRecords.toDouble() / total.toDouble() * 100).toInt()
    }

    fun getPercentForSem(semesterSummary: SemesterSummary): Int {
        val total = semesterSummary.presentRecords + semesterSummary.absentRecords + semesterSummary.unmarkedRecords
        return if (total == 0) 0 else (semesterSummary.presentRecords.toDouble() / total.toDouble() * 100).toInt()
    }
}