package com.studypulse.feat.flashcards.di

import com.studypulse.feat.flashcards.ReviewCache
import com.studypulse.feat.flashcards.data.FlashcardPackRepositoryImpl
import com.studypulse.feat.flashcards.data.FlashcardRepositoryImpl
import com.studypulse.feat.flashcards.data.FlashcardReviewRepositoryImpl
import com.studypulse.feat.flashcards.data.UserStarsRepositoryImpl
import com.studypulse.feat.flashcards.domain.FlashcardDataSignal
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import com.studypulse.feat.flashcards.domain.repository.UserStarsRepository
import com.studypulse.feat.flashcards.domain.usecase.DeleteFlashcardPackUseCase
import com.studypulse.feat.flashcards.domain.usecase.DeleteFlashcardPackUseCaseImpl
import com.studypulse.feat.flashcards.domain.usecase.GetFlashcardPackForPresentation
import com.studypulse.feat.flashcards.domain.usecase.GetFlashcardPackForPresentationImpl
import com.studypulse.feat.flashcards.domain.usecase.GetFlashcardPacksForPresentation
import com.studypulse.feat.flashcards.domain.usecase.GetFlashcardPacksForPresentationImpl
import com.studypulse.feat.flashcards.domain.usecase.GetPackCardsUseCase
import com.studypulse.feat.flashcards.domain.usecase.GetPackCardsUseCaseImpl
import com.studypulse.feat.flashcards.domain.usecase.GetReviewQueueUseCase
import com.studypulse.feat.flashcards.domain.usecase.GetReviewQueueUseCaseImpl
import com.studypulse.feat.flashcards.presentation.fcp_details.FlashcardPackDetailsScreenViewModel
import com.studypulse.feat.flashcards.presentation.fcp_list.FlashcardPackListScreenViewModel
import com.studypulse.feat.flashcards.presentation.flashcard_details.FlashcardDetailsScreenViewModel
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardEntryScreenViewModel
import com.studypulse.feat.flashcards.presentation.study_session.StudySessionScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val flashcardsModule = module {

    single<ReviewCache> { ReviewCache() }
    single { FlashcardDataSignal() }

    // usecases
    factory<GetFlashcardPackForPresentation> { GetFlashcardPackForPresentationImpl(get(), get()) }
    factory<GetFlashcardPacksForPresentation> { GetFlashcardPacksForPresentationImpl(get()) }
    factory<DeleteFlashcardPackUseCase> { DeleteFlashcardPackUseCaseImpl(get(), get()) }
    factory<GetReviewQueueUseCase> { GetReviewQueueUseCaseImpl(get(), get()) }
    factory<GetPackCardsUseCase> { GetPackCardsUseCaseImpl(get(), get()) }

    // repository
    single<FlashcardReviewRepository> { FlashcardReviewRepositoryImpl(get(), get(), get()) }
    single<FlashcardRepository> { FlashcardRepositoryImpl(get(), get(), get(), get()) }
    single<FlashcardPackRepository> { FlashcardPackRepositoryImpl(get(), get(), get()) }
    single<UserStarsRepository> { UserStarsRepositoryImpl(get(), get(), get()) }

    viewModelOf(::FlashcardEntryScreenViewModel)
    viewModelOf(::FlashcardPackListScreenViewModel)
    viewModelOf(::FlashcardPackDetailsScreenViewModel)
    viewModelOf(::FlashcardDetailsScreenViewModel)
    viewModelOf(::StudySessionScreenViewModel)
}