package com.studypulse.feat.flashcards.presentation.study_session

import com.studypulse.feat.flashcards.domain.model.FlashcardPage

data class StudySessionScreenState(
    val page: FlashcardPage = FlashcardPage(emptyList()),
    val loading: Boolean = true,
    val isPaginating: Boolean = false,
    val allFetched: Boolean = false,
)
