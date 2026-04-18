package com.studypulse.feat.flashcards.data

import com.studypulse.feat.flashcards.domain.model.Flashcard

data class FlashcardDto(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val description: String? = null,
    val packId: String = "",
    val ownerId: String = "",
) {
    fun toDomain() = Flashcard(
        id = id,
        question = question,
        answer = answer,
        description = description,
        packId = packId,
        ownerId = ownerId,
    )
}

fun Flashcard.toDto() = FlashcardDto(
    id = id,
    question = question,
    answer = answer,
    description = description,
    packId = packId,
    ownerId = ownerId,
)
