package com.studypulse.app.feat.auth.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpScreenViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth
    private val initialData = SignUpScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    fun updateEmail(new: String) {
        _state.update {
            it.copy(emailError = false, email = new, error = null)
        }
    }

    fun updatePassword(new: String) {
        _state.update {
            it.copy(passwordError = false, password = new, error = null)
        }
    }

    fun signUp(navigateToHome: () -> Unit) {
        if (credentialsOk()) {
            auth.createUserWithEmailAndPassword(_state.value.email, _state.value.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        // Sign in success, navigate user to home screen
                        navigateToHome()
                    } else {
                        _state.update {
                            it.copy(error = task.exception?.message)
                        }
                    }
                }
        }
    }

    private fun credentialsOk(): Boolean {

        if (_state.value.email.isEmpty()) {
            _state.update {
                it.copy(
                    emailError = true,
                    error = "Email cannot be empty"
                )
            }
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(_state.value.email).matches()) {
            _state.update {
                it.copy(
                    emailError = true,
                    error = "Please enter a valid email address"
                )
            }
            return false
        }


        if (_state.value.password.isEmpty()) {
            _state.update {
                it.copy(
                    emailError = false,
                    passwordError = true,
                    error = "Password cannot be empty"
                )
            }
            return false
        }


        return true
    }
}