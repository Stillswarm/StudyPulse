package com.studypulse.feat.flashcards.presentation.flashcard_entry

import androidx.lifecycle.ViewModel
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlashcardEntryScreenViewModel(
    private val fcRepository: FlashcardRepository
) : ViewModel() {

    private val initialState = FlashcardEntryScreenState()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<FlashcardEntryScreenState> = _state.asStateFlow()


}