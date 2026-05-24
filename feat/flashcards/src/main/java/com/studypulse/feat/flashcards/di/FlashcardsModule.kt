package com.studypulse.feat.flashcards.di

import com.studypulse.feat.flashcards.ReviewCache
import com.studypulse.feat.flashcards.data.FlashcardPackRepositoryImpl
import com.studypulse.feat.flashcards.data.FlashcardRepositoryImpl
import com.studypulse.feat.flashcards.data.FlashcardReviewRepositoryImpl
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import com.studypulse.feat.flashcards.presentation.fcp_details.FlashcardPackDetailsScreenViewModel
import com.studypulse.feat.flashcards.presentation.fcp_list.FlashcardPackListScreenViewModel
import com.studypulse.feat.flashcards.presentation.flashcard_details.FlashcardDetailsScreenViewModel
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardEntryScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val flashcardsModule = module {

    single<ReviewCache> { ReviewCache() }

    // repository
    single<FlashcardReviewRepository> { FlashcardReviewRepositoryImpl(get(), get()) }
    single<FlashcardRepository> { FlashcardRepositoryImpl(get(), get(), get()) }
    single<FlashcardPackRepository> { FlashcardPackRepositoryImpl(get(), get()) }

    viewModelOf(::FlashcardEntryScreenViewModel)
    viewModelOf(::FlashcardPackListScreenViewModel)
    viewModelOf(::FlashcardPackDetailsScreenViewModel)
    viewModelOf(::FlashcardDetailsScreenViewModel)
}