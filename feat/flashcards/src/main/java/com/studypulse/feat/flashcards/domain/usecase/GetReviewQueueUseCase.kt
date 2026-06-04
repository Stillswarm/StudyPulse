package com.studypulse.feat.flashcards.domain.usecase

import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardCursors
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository

/**
 * Builds a page of the study queue. Scheduling is driven entirely by the
 * per-user [com.studypulse.feat.flashcards.domain.model.FlashcardReviewState]
 * collection:
 *
 *  1. Fetch due/overdue review states (dueDate <= now), most overdue first.
 *     New cards are due immediately, so they fall in here after genuinely
 *     overdue ones.
 *  2. If the page isn't full, top it up with upcoming review states
 *     (dueDate > now), soonest first.
 *  3. Resolve the actual flashcards for those card ids in one batched read and
 *     pair each card with its review state, preserving the scheduled order.
 *
 * Pass [packId] to scope the queue to a single pack, or null to draw from all
 * of the user's cards. [cursors] carries the pagination position for both
 * stages; pass [FlashcardCursors] defaults for the first page.
 */
interface GetReviewQueueUseCase {
    suspend operator fun invoke(
        n: Long,
        packId: String? = null,
        cursors: FlashcardCursors = FlashcardCursors(),
    ): Result<FlashcardPage>
}

class GetReviewQueueUseCaseImpl(
    private val fcRepository: FlashcardRepository,
    private val frRepository: FlashcardReviewRepository,
) : GetReviewQueueUseCase {

    override suspend fun invoke(
        n: Long,
        packId: String?,
        cursors: FlashcardCursors,
    ): Result<FlashcardPage> = runCatching {
        // Stage 1: due/overdue (and, by virtue of dueDate == createdAt, new cards)
        val duePage = frRepository
            .getDueReviewStates(n, packId, cursors.lastDueNow)
            .getOrThrow()

        val orderedStates = duePage.states.toMutableList()
        var lastDueNow = duePage.lastDoc ?: cursors.lastDueNow
        var lastDueLater = cursors.lastDueLater

        // Stage 2: top up with upcoming cards if the due bucket didn't fill the page
        if (orderedStates.size < n) {
            val remaining = n - orderedStates.size
            val laterPage = frRepository
                .getUpcomingReviewStates(remaining, packId, cursors.lastDueLater)
                .getOrThrow()
            orderedStates += laterPage.states
            lastDueLater = laterPage.lastDoc ?: cursors.lastDueLater
        }

        val nextCursors = FlashcardCursors(
            lastDueNow = lastDueNow,
            lastDueLater = lastDueLater,
        )

        if (orderedStates.isEmpty()) {
            return@runCatching FlashcardPage(cards = emptyList(), cursors = nextCursors)
        }

        // Stage 3: resolve cards, then re-pair in scheduled order (whereIn is unordered)
        val cardsById = fcRepository
            .getByIds(orderedStates.map { it.cardId })
            .getOrThrow()

        val cards = orderedStates.mapNotNull { state ->
            cardsById[state.cardId]?.let { card ->
                Sm2Flashcard(flashcard = card, reviewState = state)
            }
        }

        FlashcardPage(cards = cards, cursors = nextCursors)
    }
}
