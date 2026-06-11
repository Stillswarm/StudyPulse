package com.studypulse.feat.flashcards.domain.model

import kotlinx.serialization.Serializable

data class FlashcardReviewState(
    val id: String = "",
    val userId: String = "",
    val cardId: String = "",
    val packId: String = "",
    // SM-2 fields
    val n: Int = 0,              // number of successful reviews (repetitions)
    val ef: Double = 2.5,        // easiness factor, min 1.3, starts at 2.5
    val interval: Int = 0,       // current interval in days
    val dueDate: Long = System.currentTimeMillis(),  // next review timestamp (epoch ms)
) {
    fun toDto() =
        FlashcardReviewStateDto(
            id = id,
            userId = userId,
            cardId = cardId,
            packId = packId,
            n = n,
            ef = ef,
            interval = interval,
            dueDate = dueDate
        )
}

@Serializable
data class FlashcardReviewStateDto(
    val id: String = "",
    val userId: String = "",
    val cardId: String = "",
    val packId: String = "",
    val n: Int = 0,
    val ef: Double = 2.5,
    val interval: Int = 0,
    val dueDate: Long = System.currentTimeMillis()
) {
    fun toDomain() =
        FlashcardReviewState(
            id = id,
            userId = userId,
            cardId = cardId,
            packId = packId,
            n = n,
            ef = ef,
            interval = interval,
            dueDate = dueDate
        )
}
