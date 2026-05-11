package com.studypulse.feat.flashcards.presentation.fcp_details

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FlashcardPackDetailsScreenViewModel : ViewModel() {

    private val initialData = FlashcardPackDetailsScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
}