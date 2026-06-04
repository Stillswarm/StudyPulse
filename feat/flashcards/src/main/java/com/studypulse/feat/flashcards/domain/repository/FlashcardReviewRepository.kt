package com.studypulse.feat.flashcards.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.model.ReviewStatePage

interface FlashcardReviewRepository {

    suspend fun upsert(frs: FlashcardReviewState): Result<Unit>
    suspend fun upsertMany(frsList: List<FlashcardReviewState>): Result<Unit>
    suspend fun delete(frs: FlashcardReviewState): Result<Unit>

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