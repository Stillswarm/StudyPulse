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
        val courseName = intent.getStringExtra("courseName") ?: "Next class"
        val notifId = intent.getIntExtra("notifId", 0)

        val notification = NotificationHelper.buildClassNotification(
            context,
            courseName,
            notifId
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