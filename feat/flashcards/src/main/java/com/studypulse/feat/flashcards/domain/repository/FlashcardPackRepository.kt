package com.studypulse.feat.flashcards.domain.repository

import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import kotlinx.coroutines.flow.Flow

interface FlashcardPackRepository {

    suspend fun upsert(fcp: FlashcardPack): Result<String>

    suspend fun delete(fcp: FlashcardPack): Result<Unit>

    suspend fun getById(id: String): Result<FlashcardPack>

    suspend fun getAllForOwner(ownerId: String): Result<List<FlashcardPack>>
    fun getAllForOwnerFlow(ownerId: String): Result<Flow<List<FlashcardPack>>>
    suspend fun getNForOwner(ownerId: String, n: Long): Result<List<FlashcardPack>>
    fun getNForOwnerFlow(ownerId: String, n: Long): Result<Flow<List<FlashcardPack>>>

    suspend fun getAllForThisUser(): Result<List<FlashcardPack>>
    fun getAllForThisUserFlow(): Result<Flow<List<FlashcardPack>>>
    suspend fun getNForThisUser(n: Long): Result<List<FlashcardPack>>
    fun getNForThisUserFlow(n: Long): Result<Flow<List<FlashcardPack>>>
}