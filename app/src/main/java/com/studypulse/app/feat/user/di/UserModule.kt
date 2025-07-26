package com.studypulse.app.feat.user.di

import com.studypulse.app.feat.user.data.FirebaseUserRepositoryImpl
import com.studypulse.app.feat.user.domain.UserRepository
import com.studypulse.app.feat.user.presentation.ProfileScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> { FirebaseUserRepositoryImpl(get(), get()) }

    viewModelOf(::ProfileScreenViewModel)
}