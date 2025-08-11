package com.studypulse.app.feat.user.domain.model

import androidx.annotation.Keep

@Keep
data class UserDto(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val institution: String? = null,
)

@Keep
fun UserDto.toDomain() =
    User(
        id = id ?: "",
        email = email ?: "--",
        name = name,
        institution = institution
    )

@Keep
fun User.toDto() =
    UserDto(
        id = id,
        email = email,
        name = name,
        institution = institution
    )