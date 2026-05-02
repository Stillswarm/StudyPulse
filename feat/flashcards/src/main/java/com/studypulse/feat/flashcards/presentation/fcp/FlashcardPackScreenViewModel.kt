package com.studypulse.feat.flashcards.presentation.fcp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlashcardPackScreenViewModel : ViewModel() {

    private var _state = MutableStateFlow(FlashcardPackScreenState())
    val state: StateFlow<FlashcardPackScreenState> = _state.asStateFlow()

}