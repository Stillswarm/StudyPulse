package com.studypulse.feat.flashcards

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReviewWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters), KoinComponent {

    private val frRepository: FlashcardReviewRepository by inject()
    private val reviewCache: ReviewCache by inject()

    override suspend fun doWork(): Result {
        val pending = reviewCache.snapshot()
        if (pending.isEmpty()) return Result.success()

        val uploadResult = withContext(Dispatchers.IO) {
            frRepository.upsertMany(pending.values.toList())
        }.onFailure { Log.d("app", "upsertMany states: ${it.message}"); }

        return if (uploadResult.isSuccess) {
            // only clear what we just uploaded; anything appended during the upload survives
            reviewCache.removeKeys(pending.keys)
            Result.success()
        } else {
            Result.failure()
        }
    }
}
