package com.studypulse.app.feat.auth.signup

data class SignUpScreenState(
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
)