package com.studypulse.app.feat.auth.di

import org.koin.dsl.module
import com.studypulse.app.feat.auth.signup.SignUpScreenViewModel
import com.studypulse.app.feat.auth.signin.SignInScreenViewModel
import org.koin.core.module.dsl.viewModelOf

val authModule = module {
    viewModelOf(::SignUpScreenViewModel)
    viewModelOf(::SignInScreenViewModel)
}