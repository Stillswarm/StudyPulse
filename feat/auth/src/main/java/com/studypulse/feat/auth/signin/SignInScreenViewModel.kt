package com.studypulse.feat.auth.signin

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
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

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
}

class AndroidResourceProvider(
    private val context: Context
) : ResourceProvider {
    override fun getString(resId: Int): String = context.getString(resId)
}

class SignInScreenViewModel(
    private val authRepository: AuthRepository,
    private val app: Application,
    resourceProvider: ResourceProvider,
) : ViewModel() {
    private val initialData = SignInScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    val credentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(
            GetGoogleIdOption.Builder()
                .setServerClientId(resourceProvider.getString(R.string.oauth_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()

    companion object {
        const val RESET_COOLDOWN = 30
        const val EMPTY_EMAIL_ERROR = "Email cannot be empty"
        const val EMPTY_PASSWORD_ERROR = "Password cannot be empty"
        const val INVALID_EMAIL_ERROR = "Please enter a valid email"
    }

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

    fun signIn(activityContext: Activity?) {
        if (!credentialsOk()) return
        viewModelScope.launch {
            authRepository.signInWithEmail(_state.value.email.trim(), _state.value.password)
                .onSuccess { NotificationPermissionHelper.requestIfNeeded(app, activityContext) }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    private fun credentialsOk(): Boolean {
        if (_state.value.email.isEmpty()) {
            _state.update { it.copy(error = EMPTY_EMAIL_ERROR) }
            return false
        }

        if (incorrectEmail(_state.value.email)) {
            _state.update { it.copy(error = INVALID_EMAIL_ERROR) }
            return false
        }

        if (_state.value.password.isEmpty()) {
            _state.update { it.copy(error = EMPTY_PASSWORD_ERROR) }
            return false
        }

        return true
    }

    private fun incorrectEmail(email: String) =
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun sendPasswordResetEmail() {
        if (incorrectEmail(_state.value.bottomSheetEmail)) {
            _state.update { it.copy(error = INVALID_EMAIL_ERROR) }
            return
        }
        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(_state.value.bottomSheetEmail.trim())
                .onSuccess {
                    _state.update { it.copy(emailSent = true, counter = RESET_COOLDOWN) }
                    SnackbarController.sendEvent(SnackbarEvent(message = "Reset Email Sent"))
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message) }
                }
        }
    }

    fun resetEmailSent() {
        _state.update { it.copy(emailSent = false) }
    }

    fun decrementCounter() {
        _state.update { it.copy(counter = it.counter.dec()) }
    }

    fun handleGoogleSignIn(activityContext: Activity?, context: Context) {
        viewModelScope.launch {
            try {
                val response =
                    CredentialManager.create(context).getCredential(context, credentialRequest)
                val custom = response.credential as CustomCredential
                val googleToken = GoogleIdTokenCredential.createFrom(custom.data).idToken

                authRepository.signInWithGoogle(googleToken)
                    .onSuccess { NotificationPermissionHelper.requestIfNeeded(app, activityContext) }
                    .onFailure { e -> _state.update { it.copy(error = e.message ?: "Unknown error") } }
            } catch (e: Exception) {
                Log.e("SignInVM", "Google sign-in error: ${e.message}")
                _state.update { it.copy(error = e.localizedMessage ?: "Unknown error") }
            }
        }
    }
}
