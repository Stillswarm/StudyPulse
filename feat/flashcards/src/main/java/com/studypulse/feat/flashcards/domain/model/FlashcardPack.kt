package com.studypulse.feat.flashcards.domain.model

data class FlashcardPack(
    val id: String = "",
    val ownerId: String? = null,
    val title: String,
    val description: String? = null,
    val color: Int? = null,
    val isPublic: Boolean? = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    fun toDto() = FlashcardPackDto(
        id = id,
        ownerId = ownerId,
        title = title,
        description = description,
        color = color,
        isPublic = isPublic,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

data class FlashcardPackDto(
    val id: String = "",
    val ownerId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val color: Int? = null,
    val isPublic: Boolean? = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain() = FlashcardPack(
        id = id,
        ownerId = ownerId ?: "",
        title = title ?: "",
        description = description ?: "",
        color = color ?: 0,
        isPublic = isPublic,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}