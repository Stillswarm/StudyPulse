package com.studypulse.app.feat.semester.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.model.Semester
import com.studypulse.app.feat.semester.domain.model.SemesterName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddSemesterScreenViewModel(
    private val semesterRepository: SemesterRepository,
) : ViewModel() {
    private val initialData = AddSemesterScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    fun updateName(new: SemesterName) {
        _state.update { it.copy(name = new, errorMsg = null) }
    }

    fun updateYear(new: Int) {
        _state.update { it.copy(year = new, errorMsg = null) }
    }

    fun updateStartDate(new: LocalDate) {
        _state.update { it.copy(startDate = new, errorMsg = null) }
    }

    fun updateEndDate(new: LocalDate) {
        _state.update { it.copy(endDate = new, errorMsg = null) }
    }

    fun submit() {
        if (validateData()) {
            val s = _state.value
            viewModelScope.launch {
                semesterRepository.addActiveSemester(
                    Semester(
                        id = "",
                        name = s.name!!,
                        year = s.year!!,
                        startDate = s.startDate!!,
                        endDate = s.endDate!!,
                        isCurrent = true
                    )
                )
            }
        }
    }

    private fun validateData(): Boolean {
        val s = _state.value
        if (s.name == null || s.startDate == null || s.endDate == null || s.year == null) {
            _state.update { it.copy(errorMsg = "All fields are required") }
            return false
        }
        if (s.endDate.isBefore(LocalDate.now())) {
            _state.update { it.copy(errorMsg = "End date cannot be in the past for active semester") }
            return false
        }

        return true
    }

//    private fun loadSemesters() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            semesterRepository.getAllSemesters()
//                .onFailure { e ->
//                    _state.update { it.copy(errorMsg = e.message) }
//                }
//                .onSuccess { sl ->
//                    _state.update { it.copy(semesterList = sl) }
//                }
//            _state.update { it.copy(isLoading = false) }
//        }
//    }
}