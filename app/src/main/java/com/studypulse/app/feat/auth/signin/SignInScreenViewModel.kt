package com.studypulse.app.feat.auth.signin

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.studypulse.app.R
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.user.domain.UserRepository
import com.studypulse.app.feat.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
}

// Implementation
class AndroidResourceProvider(
    private val context: Context
) : ResourceProvider {
    override fun getString(resId: Int): String = context.getString(resId)
}

class SignInScreenViewModel(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private  val app: Application,
    resourceProvider: ResourceProvider,  // easier to mockk than the earlier application
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
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
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
            auth.signInWithEmailAndPassword(_state.value.email.trim(), _state.value.password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val exception = task.exception
                        _state.update {
                            it.copy(error = exception?.message)
                        }
                    } else {
                        // check for notification permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            ContextCompat.checkSelfPermission(
                                app,
                                POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                activityContext!!,
                                arrayOf(POST_NOTIFICATIONS),
                                REQUEST_CODE_POST_NOTIFICATIONS
                            )
                        }
                    }
                }
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

    private fun incorrectEmail(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    fun sendPasswordResetEmail() {
        if (incorrectEmail(_state.value.bottomSheetEmail)) {
            _state.update { it.copy(error = INVALID_EMAIL_ERROR) }
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

    fun handleGoogleSignIn(activityContext: Activity?, context: Context) {
        viewModelScope.launch {
            try {
                val response =
                    CredentialManager.create(context).getCredential(context, credentialRequest)
                // ➊ We know this is a federated (Google) credential
                val custom = response.credential as CustomCredential
                val googleToken = GoogleIdTokenCredential.createFrom(custom.data).idToken
                // ➋ Now hand the token off to Firebase
                signUpWithGoogle(googleToken)

                // check for notification permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        app,
                        POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activityContext!!,
                        arrayOf(POST_NOTIFICATIONS),
                        REQUEST_CODE_POST_NOTIFICATIONS
                    )
                }
            } catch (e: Exception) {
                Log.e("tag", "Error: ${e.message}")
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
                    auth.currentUser?.let { user ->
                        viewModelScope.launch {
                            userRepository.addUser(
                                User(
                                    email = user.email ?: "",
                                    id = auth.currentUser!!.uid,
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