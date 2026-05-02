package com.studypulse.feat.flashcards.di

import com.studypulse.feat.flashcards.data.FlashcardPackRepositoryImpl
import com.studypulse.feat.flashcards.data.FlashcardRepositoryImpl
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardEntryScreenViewModel
import com.studypulse.feat.flashcards.presentation.fcp.FlashcardPackScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val flashcardsModule = module {

    // repository
    single<FlashcardRepository> { FlashcardRepositoryImpl(get(), get()) }
    single<FlashcardPackRepository> { FlashcardPackRepositoryImpl(get(), get()) }

    viewModelOf(::FlashcardEntryScreenViewModel)
    viewModelOf(::FlashcardPackScreenViewModel)
}