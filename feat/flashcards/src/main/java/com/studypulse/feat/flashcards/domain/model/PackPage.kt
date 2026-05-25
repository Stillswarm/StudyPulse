package com.studypulse.feat.flashcards.domain.model

import com.google.firebase.firestore.DocumentSnapshot

/**
 * A single page of [FlashcardPack]s plus the cursor that callers should pass
 * back in to fetch the next page. Cursor lifetime is owned by the caller
 * (typically a ViewModel) so the underlying repository can stay stateless and
 * safe to reuse across screens.
 */
data class PackPage(
    val items: List<FlashcardPack>,
    val nextCursor: DocumentSnapshot? = null,
    val endReached: Boolean = false,
)
