package com.studypulse.feat.flashcards

import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState

class ReviewCache {

    private val lock = Any()
    private val cache: MutableMap<String, FlashcardReviewState> = mutableMapOf()

    fun append(item: FlashcardReviewState) = synchronized(lock) {
        cache[item.cardId] = item
    }

    // returns a defensive copy so iteration is safe outside the lock
    fun snapshot(): Map<String, FlashcardReviewState> = synchronized(lock) {
        cache.toMap()
    }

    // only removes the keys we successfully uploaded; entries appended
    // during the upload survive and get picked up on the next run
    fun removeKeys(keys: Set<String>) = synchronized(lock) {
        cache.keys.removeAll(keys)
    }

    fun isEmpty(): Boolean = synchronized(lock) { cache.isEmpty() }
}
