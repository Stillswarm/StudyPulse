package com.studypulse.app.feat.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.feat.user.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileScreenViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val initialData = ProfileScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    init {
        fetchUser()
    }

    fun fetchUser() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _state.update {
                it.copy(user = userRepository.fetchCurrentUser().getOrNull(), isLoading = false)
            }
        }
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
            fetchUser()
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
}