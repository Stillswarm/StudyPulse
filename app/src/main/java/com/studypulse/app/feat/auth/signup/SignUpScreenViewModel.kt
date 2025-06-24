package com.studypulse.app.feat.auth.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.studypulse.app.feat.user.domain.User
import com.studypulse.app.feat.user.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpScreenViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
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

    fun signUp() {
        if (credentialsOk()) {
            auth.createUserWithEmailAndPassword(_state.value.email, _state.value.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, add user to db and navigate user to home screen
                        viewModelScope.launch {
                            userRepository.addUser(
                                User(
                                    email = _state.value.email,
                                    password = _state.value.password,
                                    id = Firebase.auth.currentUser!!.uid
                                )
                            )
                        }
                        // navigation happens automatically (see: AppNavGraph, AppViewModel)
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