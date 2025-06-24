package com.studypulse.app.feat.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInScreenViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val initialData = SignInScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    private val RESET_COOLDOWN = 30

    fun updateEmail(new: String) {
        _state.update {
            it.copy(errorMsg = null, email = new, emailSent = false, counter = RESET_COOLDOWN)
        }
    }

    fun updatePassword(new: String) {
        _state.update {
            it.copy(errorMsg = null, password = new, emailSent = false, counter = RESET_COOLDOWN)
        }
    }

    fun signIn() {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(_state.value.email.trim(), _state.value.password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val exception = task.exception
                        _state.update {
                            it.copy(errorMsg = exception?.message)
                        }
                    }
                }
        }
    }

    fun sendPasswordResetEmail() {
        auth.sendPasswordResetEmail(_state.value.email.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.update {
                        it.copy(emailSent = true, counter = RESET_COOLDOWN)
                    }
                    viewModelScope.launch {
                        SnackbarController.sendEvent(SnackbarEvent(message = "Reset Email Sent"))
                    }
                } else {
                    when (val e = task.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            _state.update {
                                it.copy(errorMsg = null, emailSent = true, counter = RESET_COOLDOWN)
                            }
                            viewModelScope.launch {
                                SnackbarController.sendEvent(SnackbarEvent(message = "Reset Email Sent"))
                            }
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            _state.update {
                                it.copy(errorMsg = "Invalid email format")
                            }
                        }

                        else -> {
                            _state.update {
                                it.copy(errorMsg = e?.message)
                            }
                        }
                    }
                }
            }
    }

    fun resetEmailSent() {
        _state.update {
            it.copy(emailSent = false)
        }
    }

    fun decrementCounter() {
        _state.update {
            it.copy(counter = it.counter.dec())
        }
    }
}