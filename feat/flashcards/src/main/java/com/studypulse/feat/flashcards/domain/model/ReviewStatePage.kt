package com.studypulse.feat.flashcards.domain.model

import com.google.firebase.firestore.DocumentSnapshot

/**
 * One page of [FlashcardReviewState]s plus the raw [lastDoc] snapshot to pass
 * back in as the cursor for the next page. The snapshot lives here (rather than
 * a domain-only cursor) because Firestore pagination requires the original
 * [DocumentSnapshot] for `startAfter`.
 */
data class ReviewStatePage(
    val states: List<FlashcardReviewState>,
    val lastDoc: DocumentSnapshot? = null,
)
