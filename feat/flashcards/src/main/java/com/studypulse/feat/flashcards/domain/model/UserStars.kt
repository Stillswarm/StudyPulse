package com.studypulse.feat.flashcards.domain.model

import androidx.annotation.Keep

@Keep
data class UserStars(
    val id: String = "",
    val packId: String = "",
    val starredBy: String = "",
)