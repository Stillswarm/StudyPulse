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
    // Mirrors the owning pack's visibility. Denormalized onto the card so that
    // cross-owner collection-group reads can be authorized by a query filter
    // (`whereEqualTo("public", true)`) — Firestore security rules cannot use
    // get() to inspect the pack for list queries. Kept in sync on card writes
    // and whenever the pack's visibility changes.
    val public: Boolean = false,
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
        public = public,
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
    public = public,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
