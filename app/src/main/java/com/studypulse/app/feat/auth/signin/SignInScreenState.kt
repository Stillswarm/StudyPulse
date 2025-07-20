package com.studypulse.app.feat.auth.signin

data class SignInScreenState(
    val email: String = "",
    val password: String = "",
    val errorMsg: String? = null,
    val emailSent: Boolean = false,
    val counter: Int = 30,
    val error: String? = null,
)
