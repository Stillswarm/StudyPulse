package com.studypulse.app.feat.attendance.attendance.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import com.studypulse.app.feat.semester.domain.model.Semester
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
import java.time.LocalDate

class AttendanceScreenViewModel(
    private val semesterSummaryRepository: SemesterSummaryRepository,
    private val courseSummaryRepository: CourseSummaryRepository,
    private val semesterRepository: SemesterRepository,
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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            fetchSemesterList()
            launch { fetchInitialData() }
            _state.update { it.copy(isLoading = false) }
        }
    }

    suspend fun fetchInitialData() {
        _state.update { it.copy(isLoading = true) }
        if (semesterIdFlow.value != "") {
            supervisorScope {
                Log.d("tag", "fetching stat box data")
                val semSummaryDataDef = async { semesterSummaryRepository.get() }
                val courseDataDef = async { courseSummaryRepository.getSummaryForAllCourses() }
                val semDataDef = async { semesterRepository.getAllSemesters() }
                val activeSemDef = async { semesterRepository.getActiveSemester() }

                val courseData = courseDataDef.await().getOrElse { error ->
                    Log.d("tag", error.message ?: "unknown error in course")
                    SnackbarController.sendEvent(
                        SnackbarEvent("Failed to fetch courses summary: ${error.message ?: "Unknown error"}")
                    )
                    _state.update { it.copy(isLoading = false) }
                    return@supervisorScope
                }
                val semSummaryData = semSummaryDataDef.await().getOrElse { error ->
                    SnackbarController.sendEvent(
                        SnackbarEvent("Failed to fetch semester summary: ${error.message ?: "Unknown error"}")
                    )
                    _state.update { it.copy(isLoading = false) }
                    return@supervisorScope
                }
                val semData = semDataDef.await().getOrElse { e ->
                    SnackbarController.sendEvent(
                        SnackbarEvent("Failed to fetch semester list: ${e.message ?: "Unknown error"}")
                    )
                    _state.update { it.copy(isLoading = false) }
                    return@supervisorScope
                }
                val activeSem = activeSemDef.await().getOrElse { e ->
                    SnackbarController.sendEvent(
                        SnackbarEvent("Failed to fetch active semester: ${e.message ?: "Unknown error"}")
                    )
                    _state.update { it.copy(isLoading = false) }
                    return@supervisorScope
                }
                _state.update {
                    it.copy(
                        unmarkedCount = semSummaryData.unmarkedRecords,
                        courseWiseSummaries = courseData,
                        fullAttendanceCount = courseData.filter { entry -> getPercent(entry) == 100 }.size,
                        lowAttendanceCount = courseData.filter { entry -> getPercent(entry) < entry.minAttendance }.size,
                        attendancePercentage = getPercentForSem(semSummaryData),
                        isLoading = false,
                        activeSemester = activeSem,
                        semesterList = semData
                    )
                }
            }
        }
    }

    suspend fun fetchSemesterList() {
        semesterRepository.getAllSemesters().onSuccess { semList ->
            _state.update { it.copy(semesterList = semList) }
        }
    }

    fun getPercent(courseSummary: CourseSummary): Int {
        val total =
            courseSummary.presentRecords + courseSummary.absentRecords + courseSummary.unmarkedRecords
        Log.d("tag", "total: $total, present: ${courseSummary.presentRecords}")
        return if (total == 0) 0 else (courseSummary.presentRecords.toDouble() / total.toDouble() * 100).toInt()
    }

    fun getPercentForSem(semesterSummary: SemesterSummary): Int {
        val total =
            semesterSummary.presentRecords + semesterSummary.absentRecords + semesterSummary.unmarkedRecords
        return if (total == 0) 0 else (semesterSummary.presentRecords.toDouble() / total.toDouble() * 100).toInt()
    }

    fun onChangeActiveSemester(new: Semester) {
        if (new == _state.value.activeSemester) return
        viewModelScope.launch {
            if (new.endDate < LocalDate.now()) {
                SnackbarController.sendEvent(SnackbarEvent("Active semester cannot be in the past"))
            } else {
                semesterRepository.markCurrent(new.id)
                    .onSuccess { _state.update { it.copy(activeSemester = new) } }
            }
        }
    }
}