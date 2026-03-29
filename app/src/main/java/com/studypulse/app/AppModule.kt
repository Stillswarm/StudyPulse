package com.studypulse.app

import com.studypulse.feat.auth.di.authModule
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.studypulse.core.semester.datastore.AppDatastore
import com.studypulse.feat.attendance.di.attendanceModule
import com.studypulse.app.feat.feedback.FeedbackScreenViewModel
import com.studypulse.app.feat.feedback.data.FeedbackRepository
import com.studypulse.app.feat.feedback.data.FeedbackRepositoryImpl
import com.studypulse.core.firebase.firebaseModule
import com.studypulse.feat.semester.di.semesterModule
import com.studypulse.feat.user.di.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(
        attendanceModule,
        authModule,
        userModule,
        semesterModule,
        firebaseModule,
    )

    // DataStore
    single<AppDatastore> {
        AppDatastore(androidContext())
    }

    // Firebase
    single<FirebaseAuth> { Firebase.auth }
    single<FirebaseFirestore> { Firebase.firestore }

    // Feedback Repository
    single<FeedbackRepository> { FeedbackRepositoryImpl(get(), get()) }

    // VM
    viewModelOf(::AppViewModel)
    viewModelOf(::FeedbackScreenViewModel)
}
