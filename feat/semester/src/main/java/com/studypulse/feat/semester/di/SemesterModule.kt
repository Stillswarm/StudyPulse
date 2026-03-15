package com.studypulse.feat.semester.di

import com.studypulse.core.semester.repository.SemesterRepository
import com.studypulse.core.semester.repository.SemesterSummaryRepository
import com.studypulse.feat.semester.data.FirebaseSemesterRepositoryImpl
import com.studypulse.feat.semester.data.FirebaseSemesterSummaryRepositoryImpl
import com.studypulse.feat.semester.presentation.AddSemesterScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val semesterModule = module {
    single<SemesterRepository> { FirebaseSemesterRepositoryImpl(get(), get(), get(), get()) }
    single<SemesterSummaryRepository> { FirebaseSemesterSummaryRepositoryImpl(get(), get(), get()) }

    viewModelOf(::AddSemesterScreenViewModel)
}
