package com.studypulse.app.feat.user.domain

data class User(
    val id: String,
    val email: String,
    val password: String,
    val name: String? = null,
    val institution: String? = null,
)
