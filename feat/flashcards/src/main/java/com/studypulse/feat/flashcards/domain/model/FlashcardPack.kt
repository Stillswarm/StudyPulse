package com.studypulse.feat.flashcards.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.Serializable
import kotlin.random.Random

const val CYAN_ARGB = -11230031

data class FlashcardPack(
    val id: String = "",
    val ownerId: String? = null,
    val title: String,
    val description: String? = null,
    val color: Color = randomDarkColor(),
    val fcCount: Int = 0,
    val starCount: Int = 0,
    val isPublic: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    val isStarredByUser: Boolean = false,
) {
    fun toDto() = FlashcardPackDto(
        id = id,
        ownerId = ownerId,
        title = title,
        description = description,
        color = color.toArgb(),
        isPublic = isPublic,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

@Serializable
data class FlashcardPackDto(
    val id: String = "",
    val ownerId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val color: Int? = null,
    val fcCount: Int = 0,
    val starCount: Int = 0,
    val isPublic: Boolean? = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = FlashcardPack(
        id = id,
        ownerId = ownerId ?: "",
        title = title ?: "",
        description = description ?: "",
        color = Color(color ?: CYAN_ARGB),
        fcCount = fcCount,
        starCount = starCount,
        isPublic = isPublic ?: false,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun randomDarkColor(): Color {
    val hue = Random.nextFloat() * 360f        // 0–360, any hue
    val saturation = Random.nextFloat() * 0.5f + 0.5f  // 0.5–1.0, vivid
    val lightness = Random.nextFloat() * 0.25f + 0.15f // 0.15–0.40, dark

    return Color.hsl(hue, saturation, lightness)
}