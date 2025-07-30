package com.studypulse.app.feat.attendance.notification

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class ClassAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val periodId   = intent.getStringExtra("periodId")   ?: return
        val courseId   = intent.getStringExtra("courseId")   ?: return
        val courseName = intent.getStringExtra("courseName") ?: "Upcoming Class"
        val semesterId = intent.getStringExtra("semesterId") ?: return
        val userId     = intent.getStringExtra("userId")     ?: return
        val notifId    = intent.getIntExtra("notifId", 0)

        val notification = NotificationHelper.buildClassNotification(
            context     = context,
            courseName  = courseName,
            notifId     = notifId,
            periodId    = periodId,
            courseId    = courseId,
            semesterId  = semesterId,
            userId      = userId
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            @SuppressLint("InlinedApi")
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                try {
                    // send out notification
                    Log.d("ClassAlarmReceiver", "Sending notification...")
                    NotificationManagerCompat.from(context).notify(notifId, notification)
                } catch (se: SecurityException) {
                    Log.d("ClassAlarmReceiver", "Security Exception: ${se.message}")
                }
            } else {
                Log.d("ClassAlarmReceiver", "Notification permission not granted")
            }
        } else {
            NotificationManagerCompat.from(context).notify(notifId, notification)
        }
    }
}