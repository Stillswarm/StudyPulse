package com.studypulse.feat.flashcards.domain.model

import com.studypulse.feat.flashcards.data.Sm2Flashcard
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

fun Sm2Flashcard.afterReview(q: Int): Sm2Flashcard {
    val (_, _, _, n, ef, interval, _) = this.reviewState
    val newEf = (ef + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))).coerceAtLeast(1.3)
    return if (q < 3) {
        copy(reviewState = reviewState.copy(n = 0, ef = newEf, interval = 1,
            dueDate = System.currentTimeMillis() + DAY_MS))
    } else {
        val newInterval = when (n) {
            0 -> 1
            1 -> 6
            else -> (interval * ef).roundToInt()
        }
        copy(reviewState = reviewState.copy(n = n + 1, ef = newEf, interval = newInterval,
            dueDate = System.currentTimeMillis() + newInterval * DAY_MS))
    }
}