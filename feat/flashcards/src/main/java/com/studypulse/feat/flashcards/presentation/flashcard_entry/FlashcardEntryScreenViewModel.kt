package com.studypulse.feat.flashcards.presentation.flashcard_entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardEntryScreenViewModel(
    private val fcRepository: FlashcardRepository,
    private val fcpRepository: FlashcardPackRepository,
) : ViewModel() {

    private val initialState = FlashcardEntryScreenState()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<FlashcardEntryScreenState> = _state.asStateFlow()


    fun onNewFcpTitleChange(newTitle: String) {
        _state.update { it.copy(newFcpTitle = newTitle) }
    }

    fun addAndNavigate(onNavigateToFcpScreen: (id: String) -> Unit) =
        viewModelScope.launch {
            fcpRepository.upsert(FlashcardPack(title = state.value.newFcpTitle)).onFailure {
                SnackbarController.sendEvent(SnackbarEvent("Failed to create flashcard pack!"))
            }.onSuccess { id ->
                onNavigateToFcpScreen(id)
            }
        }
}