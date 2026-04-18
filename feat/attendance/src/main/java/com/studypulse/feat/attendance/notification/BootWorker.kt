package com.studypulse.feat.attendance.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.studypulse.feat.attendance.courses.domain.PeriodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params), KoinComponent {
    private val periodRepository: PeriodRepository by inject()

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO)
        {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext Result.failure()
            return@withContext try {
                val periods = periodRepository.getAllPeriods().getOrNull() ?: emptyList()

                periods.forEach { p ->
                    AlarmScheduler.scheduleAlarmForPeriod(applicationContext, p, userId)
                }

                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
}