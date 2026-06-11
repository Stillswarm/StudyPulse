package com.studypulse.feat.flashcards.domain.usecase

import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository

/**
 * Deletes a flashcard pack along with every flashcard contained within it.
 *
 * Cards are removed first so that, in the unlikely event that the pack
 * deletion fails after the cards are gone, a retry can still target the
 * orphan pack. The reverse ordering would risk leaving cards pointing at a
 * non-existent pack.
 */
interface DeleteFlashcardPackUseCase {
    suspend operator fun invoke(pack: FlashcardPack): Result<Unit>
}

class DeleteFlashcardPackUseCaseImpl(
    private val fcRepository: FlashcardRepository,
    private val fcpRepository: FlashcardPackRepository,
) : DeleteFlashcardPackUseCase {

    override suspend operator fun invoke(pack: FlashcardPack): Result<Unit> = runCatching {
        fcRepository.deleteAllByPackId(pack.id).getOrThrow()
        fcpRepository.delete(pack).getOrThrow()
    }
}
