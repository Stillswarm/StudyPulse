package com.studypulse.app

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uidState = MutableStateFlow(auth.currentUser?.uid)
    val currentUid: StateFlow<String?> = _uidState

    private val listener = FirebaseAuth.AuthStateListener { auth ->
        _uidState.value = auth.currentUser?.uid
    }

    init {
        auth.addAuthStateListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(listener)
    }
}