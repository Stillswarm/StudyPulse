package com.studypulse.app.feat.auth.model

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val institution: String,
)
