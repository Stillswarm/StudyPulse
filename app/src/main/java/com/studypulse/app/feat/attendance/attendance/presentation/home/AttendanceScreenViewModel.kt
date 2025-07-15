package com.studypulse.app.feat.attendance.attendance.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.semester.domain.SemesterRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendanceScreenViewModel(
    private val semesterRepository: SemesterRepository,
    ds: AppDatastore,
) : ViewModel() {
    private val initialData = AttendanceScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    val semesterIdFlow = ds.semesterIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000, 0),
        initialValue = ""
    )

    fun fetchStatBoxData() {
        viewModelScope.launch {
            coroutineScope {

            }
        }
    }
}