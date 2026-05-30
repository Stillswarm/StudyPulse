package com.studypulse.feat.flashcards.domain.repository

import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardCursors
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {

    // keep flashcard page data against packId
    var flashcardPageCache: MutableMap<String, FlashcardPage>

    suspend fun upsert(flashcard: Flashcard): Result<Unit>

    suspend fun getById(id: String): Result<Flashcard>

    suspend fun getAllByOwner(ownerId: String): Result<List<Flashcard>>
    suspend fun getAllByOwnerFlow(ownerId: String): Result<Flow<List<Flashcard>>>

    suspend fun getAllByPackId(packId: String): Result<List<Flashcard>>
    fun getAllByPackIdFlow(packId: String): Result<Flow<List<Flashcard>>>

    suspend fun delete(flashcard: Flashcard): Result<Unit>

    /**
     * Batch deletes every flashcard whose `packId` matches [packId].
     *
     * Implementations should chunk writes so that Firestore's 500-operation
     * batch limit is respected even for very large packs.
     */
    suspend fun deleteAllByPackId(packId: String): Result<Unit>

    suspend fun getNRandomFromSamePack(n: Long, packId: String, cursors: FlashcardCursors = FlashcardCursors()): Result<FlashcardPage>
    suspend fun getNRandomFromAcrossPacks(n: Long, cursors: FlashcardCursors = FlashcardCursors()): Result<FlashcardPage>
}