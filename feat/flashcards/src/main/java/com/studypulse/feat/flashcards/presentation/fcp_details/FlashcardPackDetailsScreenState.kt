package com.studypulse.feat.flashcards.presentation.fcp_details

import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.model.FlashcardPage

data class FlashcardPackDetailsScreenState(
    val fcp: FlashcardPack? = null,
    val flashcardPage: FlashcardPage = FlashcardPage(emptyList()),
    val isRefreshing: Boolean = false,
    val isOwner: Boolean = false,
    val canDelete: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isDeleting: Boolean = false,
    val deleted: Boolean = false,
)
