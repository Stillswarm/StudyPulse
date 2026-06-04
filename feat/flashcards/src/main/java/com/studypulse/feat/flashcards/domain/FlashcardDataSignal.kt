package com.studypulse.feat.flashcards.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

enum class FlashcardTopic { PACKS, CARDS, REVIEWS, STARS }

/**
 * In-memory, process-scoped change log. Mutating repository calls bump the
 * topics they affect; screens record the version they last loaded at and
 * refetch only when it advances. This keeps "refresh on return" cheap: a screen
 * that nothing has changed under costs zero Firestore reads.
 *
 * Not persisted: on cold start every topic is version 0, so each screen loads
 * exactly once. Detects mutations made within this process only.
 */
class FlashcardDataSignal {
    private val versions = MutableStateFlow(emptyMap<FlashcardTopic, Long>())

    fun bump(vararg topics: FlashcardTopic) {
        versions.update { current ->
            current.toMutableMap().apply {
                topics.forEach { t -> this[t] = (this[t] ?: 0L) + 1L }
            }
        }
    }

    /** Combined version of the given topics; advances whenever any of them bump. */
    fun versionOf(vararg topics: FlashcardTopic): Long {
        val snap = versions.value
        return topics.fold(0L) { acc, t -> acc + (snap[t] ?: 0L) }
    }
}
