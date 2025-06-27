package com.studypulse.app.feat.semester.di

import com.studypulse.app.feat.semester.data.FirebaseSemesterRepositoryImpl
import com.studypulse.app.feat.semester.domain.SemesterRepository
import org.koin.dsl.module

val semesterModule = module {
    // repository
    single<SemesterRepository> { FirebaseSemesterRepositoryImpl(get(), get()) }
}