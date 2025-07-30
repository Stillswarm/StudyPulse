package com.studypulse.app.feat.attendance.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.studypulse.app.common.util.toCalendarDay
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

object AlarmScheduler {
    fun scheduleAlarmForPeriod(context: Context, period: Period) {
        val now = ZonedDateTime.now()

        var nextDay = now.with(ChronoField.DAY_OF_WEEK, period.day.toCalendarDay())
            .withHour(period.startTime.hour)
            .withMinute(period.startTime.minute)
            .withSecond(0)
            .withNano(0)

        if (nextDay.isBefore(now)) {
            nextDay = nextDay.plusWeeks(1)
        }

        val triggerMs = nextDay.minusMinutes(5).toInstant().toEpochMilli()
        val intent = Intent(context, ClassAlarmReceiver::class.java).apply {
            putExtra("courseName", period.courseName)
            putExtra("notifId", period.id.hashCode())
        }

        val pi = PendingIntent.getBroadcast(
            context,
            period.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 1) Check if we’re allowed to schedule exact alarms
                if (mgr.canScheduleExactAlarms()) {
                    mgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
                } else {
                    // 2) Fallback: request the user’s help, or degrade to inexact
                    //    e.g. mgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
                    Log.w("AlarmScheduler", "Exact alarms not allowed; using inexact fallback")
                    mgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
                }
            } else {
                // pre‑S everything works as before
                mgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerMs, pi)
            }
        } catch (se: SecurityException) {
            // 3) Last resort: log it so you know something went wrong
            Log.e("AlarmScheduler", "Failed to schedule exact alarm", se)
        }
    }

    fun cancelAlarm(context: Context, periodId: String) {
        val intent = Intent(context, ClassAlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            periodId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pi != null) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pi)
            pi.cancel()
        }
    }
}