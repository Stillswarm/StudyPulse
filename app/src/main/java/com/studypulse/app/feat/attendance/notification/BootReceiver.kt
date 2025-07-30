package com.studypulse.app.feat.attendance.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.core.component.KoinComponent

class BootReceiver : BroadcastReceiver(), KoinComponent {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val workRequest = OneTimeWorkRequestBuilder<BootWorker>()
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "reschedule_class_alarms",
                    ExistingWorkPolicy.KEEP,
                    workRequest
                )
        }
    }
}