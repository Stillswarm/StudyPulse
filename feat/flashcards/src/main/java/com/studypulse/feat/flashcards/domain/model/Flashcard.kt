package com.studypulse.feat.flashcards.domain.model

import kotlin.math.roundToInt

const val DAY_MS = 86_400_000

data class Flashcard(
    val id: String,
    val question: String,
    val answer: String,
    val description: String?,
    val packId: String,
    val ownerId: String,
    val read: Boolean = false,

    // SM-2 fields
    val n: Int = 0,              // number of successful reviews (repetitions)
    val ef: Double = 2.5,        // easiness factor, min 1.3, starts at 2.5
    val interval: Int = 0,       // current interval in days
    val dueDate: Long = System.currentTimeMillis(),  // next review timestamp (epoch ms)

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

enum class FlashcardFeedback(val score: Int) {
    BLACKOUT(0),
    WRONG(2),
    HARD(3),
    OKAY(4),
    EASY(5),
}

fun Flashcard.afterReview(q: Int): Flashcard {
    val newEf = (ef + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))).coerceAtLeast(1.3)
    return if (q < 3) {
        copy(n = 0, ef = newEf, interval = 1,
            dueDate = System.currentTimeMillis() + DAY_MS)
    } else {
        val newInterval = when (n) {
            0 -> 1
            1 -> 6
            else -> (interval * ef).roundToInt()
        }
        copy(n = n + 1, ef = newEf, interval = newInterval,
            dueDate = System.currentTimeMillis() + newInterval * DAY_MS)
    }
}