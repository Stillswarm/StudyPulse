package com.studypulse.app.feat.attendance.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.studypulse.app.R

object NotificationHelper {
    const val CHANNEL_ID = "com.studypulse.app.CLASS_REMINDERS"

    fun createNotificationChannel(context: Context) {
        val name = "Class Reminders"
        val description = "Alerts you a when a new class starts"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    fun buildClassNotification(context: Context, courseName: String, notifId: Int): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_pulse)
            .setContentTitle("Upcoming Class")
            .setContentText("$courseName is about to start!")
            .setAutoCancel(true)
            .build()
    }
}