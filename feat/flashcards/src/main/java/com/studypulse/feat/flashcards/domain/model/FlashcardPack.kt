package com.studypulse.feat.flashcards.domain.model

data class FlashcardPack(
    val id: String,
    val ownerId: String,
    val title: String,
    val description: String,
    val color: Int,
)
