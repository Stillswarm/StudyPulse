package com.studypulse.app.common.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SetStatusBarColor(backgroundColor: Color) {
    val view = LocalView.current
    DisposableEffect(backgroundColor) {
        val window = (view.context as? Activity)?.window
        window?.let {
            WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = backgroundColor.luminance() > 0.5f
        }
        onDispose {

        }
    }
}