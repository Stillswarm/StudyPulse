package com.studypulse.app

import com.studypulse.app.feat.attendance.di.attendanceModule
import com.studypulse.app.feat.auth.di.authModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(
        attendanceModule,
        authModule,
    )

    // DB
    single<StudyPulseDatabase> { StudyPulseDatabase.getDatabase(get()) }

    // VM
    viewModelOf(::AppViewModel)
}