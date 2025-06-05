package com.studypulse.app.feat.attendance.attendance.presentation.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AttendanceScreenViewModel : ViewModel() {
    private val initialData = AttendanceScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()


}