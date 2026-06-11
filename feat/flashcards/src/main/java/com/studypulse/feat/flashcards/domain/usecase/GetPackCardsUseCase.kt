package com.studypulse.feat.flashcards.domain.usecase

import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository

/**
 * Loads every card in [packId] across owners (via a collection-group read) and
 * pairs each with the current user's own review state.
 *
 * This is the "browse / study a pack I don't own" path. Crucially it does NOT
 * assume the cards are unreviewed: once the user reviews a card from someone
 * else's public pack, a review state for it exists under their account, and that
 * state is what gets used here. A freshly seeded (never-reviewed) state is only
 * the fallback for cards the user genuinely hasn't touched yet — and it carries
 * the cardId/packId so the first review continues from a correct baseline.
 */
interface GetPackCardsUseCase {
    suspend operator fun invoke(packId: String): Result<List<Sm2Flashcard>>
}

class GetPackCardsUseCaseImpl(
    private val fcRepository: FlashcardRepository,
    private val frRepository: FlashcardReviewRepository,
) : GetPackCardsUseCase {

    override suspend fun invoke(packId: String): Result<List<Sm2Flashcard>> = runCatching {
        val cards = fcRepository.getAllByPackIdAcrossOwners(packId).getOrThrow()
        if (cards.isEmpty()) return@runCatching emptyList()

        val statesByCardId = frRepository
            .getByCardIds(cards.map { it.id })
            .getOrThrow()

        cards.map { card ->
            Sm2Flashcard(
                flashcard = card,
                reviewState = statesByCardId[card.id]
                    ?: FlashcardReviewState(cardId = card.id, packId = card.packId),
            )
        }
    }
}
