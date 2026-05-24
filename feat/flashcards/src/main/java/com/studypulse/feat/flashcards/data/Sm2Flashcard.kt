package com.studypulse.feat.flashcards.data

import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState

data class Sm2Flashcard(
    val flashcard: Flashcard,
    val reviewState: FlashcardReviewState
)
