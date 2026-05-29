package com.studypulse.feat.flashcards.domain.repository

import com.studypulse.feat.flashcards.domain.model.UserStars

interface UserStarsRepository {

    suspend fun starPack(packId: String): Result<Unit>

    /**
     * Remove the current user's star on [packId]. Idempotent — succeeds with
     * `Unit` even if no star document exists.
     */
    suspend fun unstarPack(packId: String): Result<Unit>

    suspend fun getPackIdsStarredByThisUser(limit: Long): Result<List<UserStars>>

    suspend fun hasUserStarred(packId: String): Result<Boolean>
}