package com.studypulse.app

import com.algolia.client.api.SearchClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.di.attendanceModule
import com.studypulse.app.feat.auth.di.authModule
import com.studypulse.app.feat.feedback.FeedbackScreenViewModel
import com.studypulse.app.feat.feedback.data.FeedbackRepository
import com.studypulse.app.feat.feedback.data.FeedbackRepositoryImpl
import com.studypulse.app.feat.semester.di.semesterModule
import com.studypulse.app.feat.user.di.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    includes(
        attendanceModule,
        authModule,
        userModule,
        semesterModule,
    )


    // DataStore
    single<AppDatastore> {
        AppDatastore(androidContext())
    }

    // Firebase
    single<FirebaseAuth> { Firebase.auth }
    single<FirebaseFirestore> { Firebase.firestore }

    // DB
    single<StudyPulseDatabase> { StudyPulseDatabase.getDatabase(get()) }

    // Feedback Repository
    single<FeedbackRepository> { FeedbackRepositoryImpl(get(), get()) }

    // Algolia Client
    single { SearchClient(BuildConfig.ALGOLIA_APP_ID, BuildConfig.ALGOLIA_SEARCH_KEY) }

    // VM
    viewModelOf(::AppViewModel)
    viewModelOf(::FeedbackScreenViewModel)
}