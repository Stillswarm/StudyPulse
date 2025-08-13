package com.studypulse.app.feat.auth.signup

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Patterns
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.studypulse.app.R
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.user.domain.UserRepository
import com.studypulse.app.feat.user.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpScreenViewModel(
    private val userRepository: UserRepository,
    private val app: Application,
) : ViewModel() {
    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
    }
    private var auth: FirebaseAuth = Firebase.auth
    private val initialData = SignUpScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()

    // ② Build once up front with your server client ID
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
                return@launch
            }
        }
        if (credentialsOk()) {
            auth.createUserWithEmailAndPassword(_state.value.email, _state.value.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, add user to db and navigate user to home screen
                        viewModelScope.launch {
                            userRepository.addUser(
                                User(
                                    email = _state.value.email,
                                    id = Firebase.auth.currentUser!!.uid
                                )
                            )
                        }

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

                        // navigation happens automatically (see: AppNavGraph, AppViewModel)
                    } else {
                        _state.update {
                            it.copy(error = task.exception?.message)
                        }
                    }
                }
        }
    }

    fun handleGoogleSignIn(activity: Activity?, context: Context) {
        viewModelScope.launch {
            if (activity == null) {
                viewModelScope.launch {
                    SnackbarController.sendEvent(SnackbarEvent("No activity found"))
                    return@launch
                }
            }
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
                        activity!!,
                        arrayOf(POST_NOTIFICATIONS),
                        REQUEST_CODE_POST_NOTIFICATIONS
                    )
                }
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
                                    id = Firebase.auth.currentUser!!.uid,
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