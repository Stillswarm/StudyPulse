package com.studypulse.app.feat.attendance.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

class AttendanceActionReceiver : BroadcastReceiver(), KoinComponent {

    private val attendanceRepository: AttendanceRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val periodId = intent.getStringExtra("periodId") ?: return
        val courseId   = intent.getStringExtra("courseId") ?: return
        val semesterId = intent.getStringExtra("semesterId") ?: return
        val dateStr    = intent.getStringExtra("date")    ?: return
        val mark       = intent.getStringExtra("mark")    ?: return
        val notifId    = intent.getIntExtra("notifId", -1)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (notifId < 0) return

        val status = when (mark) {
            "P" -> AttendanceStatus.PRESENT
            "A" -> AttendanceStatus.ABSENT
            "C" -> AttendanceStatus.CANCELLED
            else -> AttendanceStatus.UNMARKED
        }
        Log.d("AttendanceActionReceiver", "Received action: $mark, $periodId, $courseId, $dateStr, $notifId, $status, $semesterId")

        val date = LocalDate.parse(dateStr)
        val record = AttendanceRecord(
            userId = userId,
            courseId = courseId,
            semesterId = semesterId,
            periodId = periodId,
            date = date,
            status = status
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("AttendanceActionReceiver", "Updating attendance...")
                attendanceRepository.upsertAttendance(record)
                Log.d("AttendanceActionReceiver", "Updated, cancelling notif...")
                NotificationManagerCompat.from(context).cancel(notifId)
            } catch (e: Exception) {
                Log.d("AttendanceActionReceiver", "Error updating attendance: ${e.message}")
            }
        }
    }
}