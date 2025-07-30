package com.studypulse.app.feat.attendance.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
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
        withContext(Dispatchers.IO) {
            return@withContext try {
                val periods = periodRepository.getAllPeriods().getOrNull() ?: emptyList()

                periods.forEach { p ->
                    AlarmScheduler.scheduleAlarmForPeriod(applicationContext, p)
                }

                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
}