package com.studypulse.feat.flashcards.presentation.fcp_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardPackDetailsScreenViewModel(
    private val fcRepository: FlashcardRepository,
    private val fcpRepository: FlashcardPackRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialData = FlashcardPackDetailsScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val id = savedStateHandle.get<String>("id")

    init {
        fetchInitialData()
    }

    fun fetchInitialData() {
        if (id == null) return
        viewModelScope.launch {
            fcpRepository.getById(id).onSuccess { fcp ->
                _state.update { it.copy(fcp = fcp) }
            }
        }

        fcRepository.getAllByPackIdFlow(id).onSuccess { flow ->
            viewModelScope.launch {
                flow.collect { flashcards ->
                    _state.update { it.copy(flashcards = flashcards) }
                }
            }
        }
    }
}