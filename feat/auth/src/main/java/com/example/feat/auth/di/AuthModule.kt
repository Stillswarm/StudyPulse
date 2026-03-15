package com.example.feat.auth.di

import com.example.feat.auth.signin.AndroidResourceProvider
import com.example.feat.auth.signin.ResourceProvider
import com.example.feat.auth.signin.SignInScreenViewModel
import com.example.feat.auth.signup.SignUpScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {

    single<ResourceProvider> { AndroidResourceProvider(context = get()) }

    viewModelOf(::SignUpScreenViewModel)
    viewModelOf(::SignInScreenViewModel)
}