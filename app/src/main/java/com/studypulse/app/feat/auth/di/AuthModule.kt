package com.studypulse.app.feat.auth.di

import com.studypulse.app.feat.auth.signin.AndroidResourceProvider
import com.studypulse.app.feat.auth.signin.ResourceProvider
import com.studypulse.app.feat.auth.signin.SignInScreenViewModel
import com.studypulse.app.feat.auth.signup.SignUpScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {

    single<ResourceProvider> { AndroidResourceProvider(context = get()) }

    viewModelOf(::SignUpScreenViewModel)
    viewModelOf(::SignInScreenViewModel)
}