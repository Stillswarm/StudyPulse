package com.studypulse.feat.auth.util

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object NotificationPermissionHelper {
    private const val REQUEST_CODE = 1001

    fun requestIfNeeded(app: Application, activity: Activity?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(app, POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            activity?.let {
                ActivityCompat.requestPermissions(it, arrayOf(POST_NOTIFICATIONS), REQUEST_CODE)
            }
        }
    }
}
