package com.studypulse.feat.flashcards.presentation.flashcard_details

import com.studypulse.feat.flashcards.domain.model.Flashcard

data class FlashcardDetailsScreenState(
    val fc: Flashcard? = null,
    val loading: Boolean = true,
    val editing: Boolean = false,
)