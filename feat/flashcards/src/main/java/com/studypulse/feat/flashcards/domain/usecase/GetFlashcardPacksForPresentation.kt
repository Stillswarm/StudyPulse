package com.studypulse.feat.flashcards.domain.usecase

import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.repository.UserStarsRepository

interface GetFlashcardPacksForPresentation {
    suspend operator fun invoke(packs: List<FlashcardPack>): Result<List<FlashcardPack>>
}

class GetFlashcardPacksForPresentationImpl(
    private val userStarsRepository: UserStarsRepository,
) : GetFlashcardPacksForPresentation {

    override suspend operator fun invoke(
        packs: List<FlashcardPack>,
    ): Result<List<FlashcardPack>> {
        if (packs.isEmpty()) return Result.success(emptyList())

        // get all starred cards, and resolve locally
        return userStarsRepository.getPackIdsStarredByThisUser(STAR_FETCH_LIMIT)
            .map { stars ->
                val starredIds = stars.mapTo(HashSet(stars.size)) { it.packId }
                packs.map { it.copy(isStarredByUser = it.id in starredIds) }
            }
    }

    companion object {
        private const val STAR_FETCH_LIMIT = 1000L
    }
}
