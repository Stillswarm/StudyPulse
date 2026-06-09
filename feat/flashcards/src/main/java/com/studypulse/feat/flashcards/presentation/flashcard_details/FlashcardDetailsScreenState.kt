package com.studypulse.feat.flashcards.presentation.flashcard_details

import com.studypulse.feat.flashcards.data.Sm2Flashcard

data class FlashcardDetailsScreenState(
    val sm2fc: Sm2Flashcard? = null,
    val loading: Boolean = true,
    val editing: Boolean = false,
    val canEdit: Boolean = false,
    val canDelete: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val deleted: Boolean = false,
)
