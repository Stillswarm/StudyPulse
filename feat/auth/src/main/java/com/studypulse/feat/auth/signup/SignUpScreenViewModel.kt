package com.studypulse.feat.auth.signup

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.feat.auth.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
import com.studypulse.feat.auth.data.AuthRepository
import com.studypulse.feat.auth.util.NotificationPermissionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpScreenViewModel(
    private val authRepository: AuthRepository,
    private val app: Application,
) : ViewModel() {

    private val initialData = SignUpScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    val credentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setServerClientId(app.getString(R.string.oauth_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

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

    fun signUp(activityContext: Activity?) {
        if (activityContext == null) {
            viewModelScope.launch {
                SnackbarController.sendEvent(SnackbarEvent("No activity found"))
            }
            return
        }
        if (!credentialsOk()) return

        viewModelScope.launch {
            authRepository.signUpWithEmail(_state.value.email, _state.value.password)
                .onSuccess { NotificationPermissionHelper.requestIfNeeded(app, activityContext) }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun handleGoogleSignIn(activity: Activity?, context: Context) {
        viewModelScope.launch {
            if (activity == null) {
                SnackbarController.sendEvent(SnackbarEvent("No activity found"))
                return@launch
            }
            try {
                val response =
                    CredentialManager.create(context).getCredential(context, credentialRequest)
                val custom = response.credential as CustomCredential
                val googleToken = GoogleIdTokenCredential.createFrom(custom.data).idToken

                authRepository.signInWithGoogle(googleToken)
                    .onSuccess { NotificationPermissionHelper.requestIfNeeded(app, activity) }
                    .onFailure { e -> _state.update { it.copy(error = e.message ?: "Unknown error") } }
            } catch (e: GetCredentialException) {
                _state.update { it.copy(error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }

    private fun credentialsOk(): Boolean {
        if (_state.value.email.isEmpty()) {
            _state.update {
                it.copy(emailError = true, error = "Email cannot be empty")
            }
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(_state.value.email).matches()) {
            _state.update {
                it.copy(emailError = true, error = "Please enter a valid email address")
            }
            return false
        }

        if (_state.value.password.isEmpty()) {
            _state.update {
                it.copy(emailError = false, passwordError = true, error = "Password cannot be empty")
            }
            return false
        }

        return true
    }
}
