package com.studypulse.feat.flashcards.data

import com.studypulse.feat.flashcards.domain.model.Flashcard

data class FlashcardDto(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val description: String? = null,
    val packId: String = "",
    val ownerId: String = "",
    val n: Int,
    val ef: Double,
    val interval: Int,
    val dueDate: Long,
    val createdAt: Long,
    val updatedAt: Long,
) {
    fun toDomain() = Flashcard(
        id = id,
        question = question,
        answer = answer,
        description = description,
        packId = packId,
        ownerId = ownerId,
        n = n,
        ef = ef,
        interval = interval,
        dueDate = dueDate,
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
    n = n,
    ef = ef,
    interval = interval,
    dueDate = dueDate,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
