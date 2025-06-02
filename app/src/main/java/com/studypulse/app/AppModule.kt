package com.studypulse.app

import com.studypulse.app.feat.attendance.di.attendanceModule
import org.koin.dsl.module

val appModule = module {
    includes(
        attendanceModule
    )

    // DB
    single<StudyPulseDatabase> { StudyPulseDatabase.getDatabase(get()) }
}