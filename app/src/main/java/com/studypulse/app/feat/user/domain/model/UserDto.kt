package com.studypulse.app.feat.user.domain.model

data class UserDto(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val institution: String? = null,
)

fun UserDto.toDomain() =
    User(
        id = id ?: "",
        email = email ?: "--",
        name = name,
        institution = institution
    )

fun User.toDto() =
    UserDto(
        id = id,
        email = email,
        name = name,
        institution = institution
    )