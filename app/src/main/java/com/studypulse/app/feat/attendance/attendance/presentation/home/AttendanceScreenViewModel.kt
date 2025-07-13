package com.studypulse.app.feat.attendance.attendance.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.common.datastore.AppDatastore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AttendanceScreenViewModel(
    private val ds: AppDatastore,
) : ViewModel() {
    private val initialData = AttendanceScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    semId = ds.semesterIdFlow.first()
                )
            }

            _state.update {
                it.copy(isLoading = false)
            }
        }
    }
}