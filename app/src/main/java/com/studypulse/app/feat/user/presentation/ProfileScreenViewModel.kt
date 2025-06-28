package com.studypulse.app.feat.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.model.Semester
import com.studypulse.app.feat.user.domain.UserRepository
import com.studypulse.app.feat.user.domain.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileScreenViewModel(
    private val userRepository: UserRepository,
    private val semesterRepository: SemesterRepository
) : ViewModel() {
    private val initialData = ProfileScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    init {
        fetchInitialData()
    }

    fun fetchInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val userDeferred = async { fetchUser() }
            val semestersDeferred = async { fetchSemesters() }
            val currentSemester = async { fetchCurrentSemester() }

            _state.update {
                it.copy(
                    user = userDeferred.await(),
                    semesterList = semestersDeferred.await(),
                    currentSemester = currentSemester.await(),
                )
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun fetchUser(): User? {
        return userRepository.fetchCurrentUser().onFailure {
            SnackbarController.sendEvent(
                SnackbarEvent("Failed to fetch current semester")
            )
        }.getOrNull()
    }

    suspend fun fetchSemesters(): List<Semester> {
        return semesterRepository.getAllSemesters().onFailure {
            SnackbarController.sendEvent(
                SnackbarEvent("Failed to fetch semester list")
            )
        }.getOrNull() ?: emptyList()
    }

    private suspend fun fetchCurrentSemester(): Semester? {
        return semesterRepository.getActiveSemester().onFailure {
            SnackbarController.sendEvent(
                SnackbarEvent("Failed to fetch current semester")
            )
        }.getOrNull()
    }

    fun updateName(new: String) {
        _state.update {
            it.copy(currentName = new)
        }
    }

    fun saveName() {
        viewModelScope.launch {
            _state.update {
                it.copy(editingName = false, isLoading = true)
            }
            userRepository.updateName(newName = _state.value.currentName!!)
            fetchUser()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun editName() {
        _state.update {
            it.copy(currentName = _state.value.user?.name ?: "", editingName = true)
        }
    }

    fun updateInstitution(new: String) {
        _state.update { it.copy(currentInstitution = new) }
    }

    fun saveInstitution() {
        viewModelScope.launch {
            _state.update {
                it.copy(editingInstitution = false, isLoading = true)
            }
            userRepository.updateInstitution(newInstitution = _state.value.currentInstitution!!)
            fetchInitialData()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun editInstitution() {
        _state.update {
            it.copy(
                currentInstitution = _state.value.user?.institution ?: "",
                editingInstitution = true
            )
        }
    }

    fun updateCurrentSemester(new: Semester) {
        if (new == _state.value.currentSemester) return
        viewModelScope.launch {
            if (new.endDate > LocalDate.now()) {
                SnackbarController.sendEvent(SnackbarEvent("Active semester cannot be in the past"))
            } else {
                semesterRepository.markCurrent(new.id).onSuccess { _state.update { it.copy(currentSemester = new) } }
            }
        }
    }
}