package com.studypulse.app

import android.app.Application
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.studypulse.app.feat.attendance.notification.BootWorker
import com.studypulse.app.feat.attendance.notification.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class StudyPulseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@StudyPulseApplication)
            modules(appModule)
        }

        NotificationHelper.createNotificationChannel(this)
        scheduleAllAlarms()
    }

    private fun scheduleAllAlarms() {
        val work = OneTimeWorkRequestBuilder<BootWorker>().build()
        WorkManager.getInstance(this)
            .enqueueUniqueWork(
                "reschedule_class_alarms",
                ExistingWorkPolicy.KEEP,
                work
            )
    }
}