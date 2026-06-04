package com.studypulse.feat.flashcards.data

import com.studypulse.feat.flashcards.domain.model.Flashcard
import kotlinx.serialization.Serializable

@Serializable
data class FlashcardDto(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val description: String? = null,
    val packId: String = "",
    val ownerId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    fun toDomain() = Flashcard(
        id = id,
        question = question,
        answer = answer,
        description = description,
        packId = packId,
        ownerId = ownerId,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun Flashcard.toDto() = FlashcardDto(
    id = id,
    question = question,
    answer = answer,
    description = description,
    packId = packId,
    ownerId = ownerId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
