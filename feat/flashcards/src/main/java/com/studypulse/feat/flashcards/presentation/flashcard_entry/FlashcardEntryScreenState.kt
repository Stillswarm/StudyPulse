package com.studypulse.feat.flashcards.presentation.flashcard_entry

import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPack

data class FlashcardEntryScreenState(
    val quickRevisionCards: List<Flashcard> = emptyList(),
    val userPacks: List<FlashcardPack> = emptyList(),
    val popularPacks: List<FlashcardPack> = emptyList(),
)
