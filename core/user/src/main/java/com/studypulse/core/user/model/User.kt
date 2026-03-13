package com.studypulse.core.user.model

data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val institution: String? = null,
)
