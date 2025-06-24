package com.studypulse.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

val LocalCurrentUser = compositionLocalOf<String?> { null }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel = koinViewModel<AppViewModel>()
            val currentUser by appViewModel.currentUid.collectAsState()
            CompositionLocalProvider(LocalCurrentUser provides currentUser) {
                StudyPulseApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}