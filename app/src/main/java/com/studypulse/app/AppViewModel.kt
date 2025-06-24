package com.studypulse.app

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _uidState = MutableStateFlow<String?>(firebaseAuth.currentUser?.uid)
    val currentUid: StateFlow<String?> = _uidState

    private val listener = FirebaseAuth.AuthStateListener { auth ->
        _uidState.value = auth.currentUser?.uid
    }

    init {
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(listener)
    }
}