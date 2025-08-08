package com.studypulse.app.feat.attendance.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
        try {
            val periodId = intent.getStringExtra("periodId")
                ?: throw IllegalStateException("Missing periodId in intent")
            val courseId = intent.getStringExtra("courseId")
                ?: throw IllegalStateException("Missing courseId in intent")
            val semesterId = intent.getStringExtra("semesterId")
                ?: throw IllegalStateException("Missing semesterId in intent")
            val dateStr = intent.getStringExtra("date")
                ?: throw IllegalStateException("Missing date in intent")
            val mark = intent.getStringExtra("mark")
                ?: throw IllegalStateException("Missing mark in intent")
            val notifId = intent.getIntExtra("notifId", -1)
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException(
                "User not authenticated"
            )
            if (notifId < 0) throw IllegalStateException("Invalid notification id")

            val status = when (mark) {
                "P" -> AttendanceStatus.PRESENT
                "A" -> AttendanceStatus.ABSENT
                "C" -> AttendanceStatus.CANCELLED
                else -> AttendanceStatus.UNMARKED
            }
            Log.d(
                "AttendanceActionReceiver",
                "Received action: $mark, $periodId, $courseId, $dateStr, $notifId, $status, $semesterId"
            )

            val date = LocalDate.parse(dateStr)
            val record = AttendanceRecord(
                userId = userId,
                courseId = courseId,
                semesterId = semesterId,
                periodId = periodId,
                date = date,
                status = status,
                processed = true
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("AttendanceActionReceiver", "Updating attendance...")
                    attendanceRepository.findExistingRecordId(record).onSuccess {
                        attendanceRepository.upsertAttendance(record.copy(id = it))
                    }.onFailure {
                        Log.e("AttendanceActionReceiver", "Error finding existing record: ${it.message}")

                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(context, "Couldn't perform action", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Log.d("AttendanceActionReceiver", "Updated, cancelling notif...")
                    NotificationManagerCompat.from(context).cancel(notifId)
                } catch (e: Exception) {
                    Log.d("AttendanceActionReceiver", "Error updating attendance: ${e.message}")
                }
            }
        } catch (ise: IllegalStateException) {
            Log.e("AttendanceActionReceiver", "Missing/malformed intent extra: ${ise.message}")
        } catch (e: Exception) {
            Log.e("AttendanceActionReceiver", "Error processing intent: ${e.message}")
        }
    }
}