package com.studypulse.feat.flashcards.presentation.fcp_details

import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPack

data class FlashcardPackDetailsScreenState(
    val fcp: FlashcardPack? = null,
    val flashcards: List<Flashcard> = emptyList()
)
