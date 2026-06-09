package com.studypulse.feat.flashcards.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.model.ReviewStatePage

interface FlashcardReviewRepository {

    suspend fun upsert(frs: FlashcardReviewState): Result<Unit>
    suspend fun upsertMany(frsList: List<FlashcardReviewState>): Result<Unit>
    suspend fun delete(frs: FlashcardReviewState): Result<Unit>

    /**
     * Deletes the current user's review states for a single card. Used to clean
     * up after a card is removed so its review states don't dangle. Only the
     * caller's own states are affected; other users who studied the same
     * (public) card keep their independent states.
     */
    suspend fun deleteByCardId(cardId: String): Result<Unit>

    /**
     * Deletes the current user's review states for an entire pack. Used when a
     * pack is removed. As with [deleteByCardId], only the caller's own states
     * are affected.
     */
    suspend fun deleteByPackId(packId: String): Result<Unit>

    suspend fun get(cardId: String): Result<FlashcardReviewState>

    /**
     * The current user's review states for the given [cardIds], keyed by cardId.
     * Cards the user has never reviewed are simply absent from the map, so
     * callers can distinguish "reviewed" from "new" and supply their own
     * fallback rather than assuming a never-reviewed state.
     */
    suspend fun getByCardIds(cardIds: List<String>): Result<Map<String, FlashcardReviewState>>

    suspend fun getDueReviewStates(
        n: Long,
        packId: String? = null,
        cursor: DocumentSnapshot? = null,
    ): Result<ReviewStatePage>

    suspend fun getUpcomingReviewStates(
        n: Long,
        packId: String? = null,
        cursor: DocumentSnapshot? = null,
    ): Result<ReviewStatePage>
}