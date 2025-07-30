package com.studypulse.app.feat.attendance.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.studypulse.app.R
import java.time.LocalDate

object NotificationHelper {
    const val CHANNEL_ID = "com.studypulse.app.CLASS_REMINDERS"

    fun createNotificationChannel(context: Context) {
        val name = "Class Reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(CHANNEL_ID, name, importance)

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    fun buildClassNotification(
        context: Context,
        courseName: String,
        notifId: Int,
        periodId: String,
        courseId: String,
        semesterId: String,
        userId: String
    ): Notification {
        val todayString = LocalDate.now().toString()
        fun intentFor(mark: String) = Intent(context, AttendanceActionReceiver::class.java).apply {
            putExtra("periodId", periodId)
            putExtra("courseId", courseId)
            putExtra("semesterId", semesterId)
            putExtra("date", todayString)
            putExtra("mark", mark)
            putExtra("userId", userId)
            putExtra("notifId", notifId)
        }

        fun pi(mark: String, offset: Int) = PendingIntent.getBroadcast(
            context,
            notifId * 10 + offset,
            intentFor(mark),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_pulse)
            .setContentTitle("Upcoming Class")
            .setContentText("$courseName is about to start!")
            .setAutoCancel(true)
            .addAction(0, "Present", pi("P", 1))
            .addAction(0, "Absent", pi("A", 2))
            .addAction(0, "Cancelled", pi("C", 3))
            .build()
    }
}