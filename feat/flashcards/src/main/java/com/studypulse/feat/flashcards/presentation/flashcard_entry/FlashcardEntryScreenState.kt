package com.studypulse.feat.flashcards.presentation.flashcard_entry

import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.model.FlashcardPage

data class FlashcardEntryScreenState(
    val quickRevisionPage: FlashcardPage = FlashcardPage(emptyList()),
    val userPacks: List<FlashcardPack> = emptyList(),
    val popularPacks: List<FlashcardPack> = emptyList(),
    val newFcp: FlashcardPack = FlashcardPack(title = ""),
)
