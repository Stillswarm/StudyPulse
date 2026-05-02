package com.studypulse.feat.flashcards.domain.model

data class Flashcard(
    val id: String = "",
    val question: String,
    val answer: String,
    val description: String?,
    val packId: String,
    val ownerId: String,
)
