package com.studypulse.feat.flashcards.domain.repository

import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState

interface FlashcardReviewRepository {

    suspend fun upsert(frs: FlashcardReviewState): Result<Unit>
    suspend fun upsertMany(frsList: List<FlashcardReviewState>): Result<Unit>
    suspend fun delete(frs: FlashcardReviewState): Result<Unit>

    suspend fun get(cardId: String): Result<FlashcardReviewState>
}