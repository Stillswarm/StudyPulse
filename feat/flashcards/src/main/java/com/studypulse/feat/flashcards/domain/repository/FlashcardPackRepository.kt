package com.studypulse.feat.flashcards.domain.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.model.PackPage
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

    /**
     * Fetch up to [n] packs owned by the current user, ordered by `updatedAt`
     * descending. Pass `null` as [cursor] for the first page and the
     * [PackPage.nextCursor] returned from the previous call to continue.
     */
    suspend fun getNForThisUser(n: Long, cursor: DocumentSnapshot? = null): Result<PackPage>
    fun getNForThisUserFlow(n: Long): Result<Flow<List<FlashcardPack>>>

    /**
     * Fetch up to [limit] public packs ordered by `starCount` descending. Pass
     * `null` as [cursor] for the first page and the [PackPage.nextCursor]
     * returned from the previous call to continue.
     */
    suspend fun getPopularPacks(limit: Long, cursor: DocumentSnapshot? = null): Result<PackPage>
    fun getPopularPacksFlow(limit: Long): Result<Flow<List<FlashcardPack>>>
}