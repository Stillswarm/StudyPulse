package com.studypulse.app

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.studypulse.app.feat.attendance.di.attendanceModule
import com.studypulse.app.feat.auth.di.authModule
import com.studypulse.app.feat.user.di.userModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(
        attendanceModule,
        authModule,
        userModule,
    )

    // Firestore
    single<FirebaseFirestore> { Firebase.firestore }

    // DB
    single<StudyPulseDatabase> { StudyPulseDatabase.getDatabase(get()) }

    // VM
    viewModelOf(::AppViewModel)
}