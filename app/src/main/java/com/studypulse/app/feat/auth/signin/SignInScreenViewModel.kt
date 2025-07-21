package com.studypulse.app.feat.auth.signin

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.studypulse.app.R
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.user.domain.UserRepository
import com.studypulse.app.feat.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInScreenViewModel(
    private val userRepository: UserRepository,
    app: Application,
) : ViewModel() {
    private val auth = Firebase.auth
    private val initialData = SignInScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    val credentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setServerClientId(app.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()


    private val RESET_COOLDOWN = 30

    fun updateEmail(new: String) {
        _state.update {
            it.copy(error = null, email = new, emailSent = false, counter = RESET_COOLDOWN)
        }
    }

    fun updateBottomSheetEmail(new: String) {
        _state.update {
            it.copy(
                error = null,
                bottomSheetEmail = new,
                emailSent = false,
                counter = RESET_COOLDOWN
            )
        }
    }

    fun updatePassword(new: String) {
        _state.update {
            it.copy(error = null, password = new, emailSent = false, counter = RESET_COOLDOWN)
        }
    }

    fun signIn() {
        if (!credentialsOk()) return
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(_state.value.email.trim(), _state.value.password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val exception = task.exception
                        _state.update {
                            it.copy(error = exception?.message)
                        }
                    }
                }
        }
    }

    private fun credentialsOk(): Boolean {

        if (_state.value.email.isEmpty()) {
            _state.update { it.copy(error = "Email cannot be empty") }
            return false
        }

        if (incorrectEmail(_state.value.email)) {
            _state.update { it.copy(error = "Please enter a valid email") }
            return false
        }


        if (_state.value.password.isEmpty()) {
            _state.update { it.copy(error = "Password cannot be empty") }
            return false
        }

        return true
    }

    private fun incorrectEmail(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun sendPasswordResetEmail() {
        if (incorrectEmail(_state.value.bottomSheetEmail)) {
            _state.update { it.copy(error = "Please enter a valid email") }
            return
        }
        auth.sendPasswordResetEmail(_state.value.bottomSheetEmail.trim())
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
                                it.copy(error = null, emailSent = true, counter = RESET_COOLDOWN)
                            }
                            viewModelScope.launch {
                                SnackbarController.sendEvent(SnackbarEvent(message = "Reset Email Sent"))
                            }
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            _state.update {
                                it.copy(error = "Invalid email format")
                            }
                        }

                        else -> {
                            _state.update {
                                it.copy(error = e?.message)
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

    fun handleGoogleSignIn(context: Context) {
        viewModelScope.launch {
            try {
                val response =
                    CredentialManager.create(context).getCredential(context, credentialRequest)
                // ➊ We know this is a federated (Google) credential
                val custom = response.credential as CustomCredential
                val googleToken = GoogleIdTokenCredential.createFrom(custom.data).idToken
                // ➋ Now hand the token off to Firebase
                signUpWithGoogle(googleToken)
            } catch (e: GetCredentialException) {
                _state.update { it.copy(error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }

    fun signUpWithGoogle(
        idToken: String,
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.update { it.copy(error = "success ${auth.currentUser?.displayName}") }
                    auth.currentUser?.let { user ->
                        viewModelScope.launch {
                            userRepository.addUser(
                                User(
                                    email = user.email ?: "",
                                    id = com.google.firebase.Firebase.auth.currentUser!!.uid,
                                    name = user.displayName ?: "",
                                )
                            )
                        }
                    }
                } else {
                    task.exception?.let { e ->
                        _state.update { it.copy(error = e.message ?: "Unknown error") }
                    }
                }
            }
    }
}