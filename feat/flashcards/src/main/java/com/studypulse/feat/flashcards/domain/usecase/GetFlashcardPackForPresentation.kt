package com.studypulse.feat.flashcards.domain.usecase

import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.UserStarsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

interface GetFlashcardPackForPresentation {
    suspend operator fun invoke(packId: String): Result<FlashcardPack>
}

class GetFlashcardPackForPresentationImpl(
    private val fcpRepository: FlashcardPackRepository,
    private val userStarsRepository: UserStarsRepository,
) : GetFlashcardPackForPresentation {
    override suspend operator fun invoke(packId: String): Result<FlashcardPack> {

        return coroutineScope {
            val hasUserStarred = async { userStarsRepository.hasUserStarred(packId).getOrNull() }

            val fcp = fcpRepository.getById(packId).getOrNull()

            if (fcp != null) {
                val newFcp = fcp.copy(isStarredByUser = hasUserStarred.await() ?: false)
                Result.success(newFcp)
            } else {
                Result.failure(NoSuchElementException())
            }
        }

    }
}