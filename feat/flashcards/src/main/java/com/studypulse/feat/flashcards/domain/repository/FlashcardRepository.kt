package com.studypulse.feat.flashcards.domain.repository

import com.studypulse.feat.flashcards.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {

    suspend fun upsert(flashcard: Flashcard): Result<Unit>

    suspend fun getById(id: String): Result<Flashcard>

    suspend fun getAllByOwner(ownerId: String): Result<List<Flashcard>>
    suspend fun getAllByOwnerFlow(ownerId: String): Result<Flow<List<Flashcard>>>

    suspend fun getAllByPackId(packId: String): Result<List<Flashcard>>
    suspend fun getAllByPackIdFlow(packId: String): Result<Flow<List<Flashcard>>>

    suspend fun delete(flashcard: Flashcard): Result<Unit>

    suspend fun getNRandom(n: Long): Result<List<Flashcard>>
}