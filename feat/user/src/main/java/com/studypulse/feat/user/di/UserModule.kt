package com.studypulse.feat.user.di

import com.studypulse.core.user.repository.UserRepository
import com.studypulse.feat.user.data.FirebaseUserRepositoryImpl
import com.studypulse.feat.user.presentation.ProfileScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> { FirebaseUserRepositoryImpl(get(), get()) }

    viewModelOf(::ProfileScreenViewModel)
}