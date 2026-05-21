package com.studypulse.feat.flashcards.data

import com.studypulse.feat.flashcards.domain.model.Flashcard

data class FlashcardDto(
    val id: String = "",
    val question: String = "",
    val answer: String = "",
    val description: String? = null,
    val packId: String = "",
    val ownerId: String = "",
    val read: Boolean = false,
    val n: Int = 0,
    val ef: Double = 2.5,
    val interval: Int = 0,
    val dueDate: Long = System.currentTimeMillis(),
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
        read = read,
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
    read = read,
    n = n,
    ef = ef,
    interval = interval,
    dueDate = dueDate,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
