package com.studypulse.app.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LinearProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .height(height)
                .background(Color.LightGray)
        )

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxWidth(progress)
                .height(height)
                .background(color)
        )
    }
}