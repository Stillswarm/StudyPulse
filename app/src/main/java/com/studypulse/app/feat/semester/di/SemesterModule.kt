package com.studypulse.app.feat.semester.di

import com.studypulse.app.feat.semester.data.FirebaseSemesterRepositoryImpl
import com.studypulse.app.feat.semester.data.FirebaseSemesterSummaryRepositoryImpl
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.SemesterSummaryRepository
import com.studypulse.app.feat.semester.presentation.AddSemesterScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val semesterModule = module {
    // repository
    single<SemesterRepository> { FirebaseSemesterRepositoryImpl(get(), get(), get(), get()) }
    single<SemesterSummaryRepository> { FirebaseSemesterSummaryRepositoryImpl(get(), get(), get()) }

    // VM
    viewModelOf(::AddSemesterScreenViewModel)
}