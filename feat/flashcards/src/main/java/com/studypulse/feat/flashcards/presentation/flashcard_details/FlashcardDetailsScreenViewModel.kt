package com.studypulse.feat.flashcards.presentation.flashcard_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
import com.studypulse.feat.flashcards.domain.model.afterReview
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardDetailsScreenViewModel(
    val fcRepository: FlashcardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialData = FlashcardDetailsScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val id = savedStateHandle.get<String>("id")
    val isEditing = savedStateHandle.get<Boolean>("isEditing")

    init {
        _state.update { it.copy(editing = isEditing ?: false) }
        loadInitialFc()
    }

    fun loadInitialFc() {
        if (id == null) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }

            fcRepository.getById(id).onFailure { e ->
                SnackbarController.sendEvent(SnackbarEvent("Load Failed: ${e.localizedMessage}"))
            }.onSuccess { fc ->
                _state.update { it.copy(fc = fc) }
            }

            _state.update { it.copy(loading = false) }
        }
    }

    fun updateQuestion(new: String) {
        _state.update { it.copy(fc = state.value.fc?.copy(question = new)) }
    }

    fun updateAnswer(new: String) {
        _state.update { it.copy(fc = state.value.fc?.copy(answer = new)) }
    }

    fun updateDescription(new: String) {
        _state.update { it.copy(fc = state.value.fc?.copy(description = new)) }
    }

    fun submitEdit() {
        viewModelScope.launch {
            state.value.fc?.let { fc ->
                fcRepository.upsert(fc)
            }
        }
    }

    fun toggleEditing() {
        _state.update { it.copy(editing = !state.value.editing) }
    }

    fun submitFeedback(score: Int) {
        _state.value.fc?.let { fc ->
            viewModelScope.launch {
                fcRepository.upsert(fc.afterReview(score))
            }
        }
    }
}