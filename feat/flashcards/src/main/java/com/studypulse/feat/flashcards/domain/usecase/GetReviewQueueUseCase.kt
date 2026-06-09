package com.studypulse.feat.flashcards.domain.usecase

import com.google.firebase.firestore.DocumentSnapshot
import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardCursors
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.model.ReviewStatePage
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

    private companion object {
        // Safety bound on how many extra pages we'll scan past orphaned review
        // states before giving up, so a backlog of dangling states can't turn a
        // single queue fetch into an unbounded read loop.
        const val MAX_PAGES_PER_STAGE = 10
    }

    override suspend fun invoke(
        n: Long,
        packId: String?,
        cursors: FlashcardCursors,
    ): Result<FlashcardPage> = runCatching {
        // Stage 1: due/overdue (and, by virtue of dueDate == createdAt, new cards)
        val (dueCards, lastDueNow) = collectResolvable(
            target = n,
            startCursor = cursors.lastDueNow,
        ) { cursor -> frRepository.getDueReviewStates(n, packId, cursor) }

        val resolved = dueCards.toMutableList()
        var lastDueLater = cursors.lastDueLater

        // Stage 2: top up with upcoming cards if the due bucket didn't fill the page
        if (resolved.size < n) {
            val remaining = n - resolved.size
            val (laterCards, laterCursor) = collectResolvable(
                target = remaining,
                startCursor = cursors.lastDueLater,
            ) { cursor -> frRepository.getUpcomingReviewStates(remaining, packId, cursor) }
            resolved += laterCards
            lastDueLater = laterCursor
        }

        FlashcardPage(
            cards = resolved,
            cursors = FlashcardCursors(lastDueNow = lastDueNow, lastDueLater = lastDueLater),
        )
    }

    /**
     * Pulls review-state pages starting at [startCursor] and pairs each with its
     * flashcard, until [target] cards are resolved or the source is exhausted.
     *
     * Review states can dangle: deleting a card or pack does not remove the
     * per-user review states that referenced it, and some legacy states carry a
     * blank cardId. Such states are skipped (never shown, never counted) and we
     * page past them so they don't starve the queue. The returned cursor points
     * at the last state actually consumed, so pagination continues correctly.
     */
    private suspend fun collectResolvable(
        target: Long,
        startCursor: DocumentSnapshot?,
        fetchPage: suspend (DocumentSnapshot?) -> Result<ReviewStatePage>,
    ): Pair<List<Sm2Flashcard>, DocumentSnapshot?> {
        val resolved = mutableListOf<Sm2Flashcard>()
        var cursor = startCursor
        var pages = 0

        while (resolved.size < target && pages < MAX_PAGES_PER_STAGE) {
            pages++
            val page = fetchPage(cursor).getOrThrow()
            if (page.states.isEmpty()) break
            cursor = page.lastDoc ?: cursor

            val validStates = page.states.filter { it.cardId.isNotBlank() }
            if (validStates.isNotEmpty()) {
                val cardsById = fcRepository
                    .getByIds(validStates.map { it.cardId })
                    .getOrThrow()
                // Preserve the scheduled order (whereIn returns cards unordered).
                validStates.forEach { state ->
                    cardsById[state.cardId]?.let { card ->
                        resolved += Sm2Flashcard(flashcard = card, reviewState = state)
                    }
                }
            }

            // A short page means the source is exhausted; stop paging.
            if (page.states.size < target) break
        }

        return resolved to cursor
    }
}
