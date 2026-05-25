package com.studypulse.ui.modifier

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithCache

fun Modifier.shimmer(
    durationMillis: Int = 1200,
    colors: List<Color> = DefaultShimmerColors,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer-progress",
    )

    drawWithCache {
        // The gradient is twice as wide as the element and slides across it,
        // creating the classic "sweep" highlight effect.
        val width = size.width
        val bandWidth = width
        val startX = -bandWidth + progress * (width + bandWidth)
        val brush = Brush.linearGradient(
            colors = colors,
            start = Offset(startX, 0f),
            end = Offset(startX + bandWidth, 0f),
        )

        onDrawWithContent {
            drawContent()
            drawRect(brush = brush)
        }
    }
}

private val DefaultShimmerColors = listOf(
    Color(0xFFE6E8EB),
    Color(0xFFF3F5F7),
    Color(0xFFE6E8EB),
)
