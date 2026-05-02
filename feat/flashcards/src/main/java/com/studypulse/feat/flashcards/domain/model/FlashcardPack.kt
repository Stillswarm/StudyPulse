package com.studypulse.feat.flashcards.domain.model

data class FlashcardPack(
    val id: String = "",
    val ownerId: String,
    val title: String,
    val description: String?,
    val color: Int,
) {
    fun toDto() = FlashcardPackDto(
        id = id,
        ownerId = ownerId,
        title = title,
        description = description,
        color = color
    )
}

data class FlashcardPackDto(
    val id: String = "",
    val ownerId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val color: Int? = null,
) {
    fun toDomain() = FlashcardPack(
        id = id,
        ownerId = ownerId ?: "",
        title = title ?: "",
        description = description ?: "",
        color = color ?: 0
    )
}