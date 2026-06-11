package com.studypulse.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.studypulse.feat.flashcards.ReviewCache
import com.studypulse.feat.flashcards.ReviewWorker
import com.studypulse.ui.theme.StudyPulseTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

val LocalCurrentUser = compositionLocalOf<String?> { null }

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = 0x000000, // 20% black for light content over light background
                darkScrim = 0x2000000 // 70% black for light content over dark background
            )
        )
        setContent {
            val appViewModel = koinViewModel<AppViewModel>()
            val currentUser by appViewModel.currentUid.collectAsState()
            CompositionLocalProvider(LocalCurrentUser provides currentUser) {
                StudyPulseTheme {
                    StudyPulseApp()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseFirestore.setLoggingEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        if (isChangingConfigurations) return

        val workManager = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReviewWorker>()
            .setConstraints(constraints)
            .build()

        val res = workManager.enqueueUniqueWork("review_work", ExistingWorkPolicy.KEEP, workRequest)
        Log.d("app", "review_work result " + res.toString())
    }
}