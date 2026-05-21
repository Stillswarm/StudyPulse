package com.studypulse.feat.flashcards.domain.model

import com.google.firebase.firestore.DocumentSnapshot

data class FlashcardPage(
    val cards: List<Flashcard>,
    val cursors : FlashcardCursors = FlashcardCursors()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlashcardPage) return false
        return cards == other.cards   // only compare cards, not cursors
    }

    override fun hashCode() = cards.hashCode()
}

data class FlashcardCursors(
    val lastDueNow: DocumentSnapshot? = null,
    val lastNewCard: DocumentSnapshot? = null,
    val lastDueLater: DocumentSnapshot? = null,
)