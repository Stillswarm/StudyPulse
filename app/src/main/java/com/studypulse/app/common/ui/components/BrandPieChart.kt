package com.studypulse.app.common.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BrandPieChart(
    values: List<Float>,
    colors: List<Color> = defaultPieColors,
    size: Dp = 200.dp,
    strokeWidth: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    val total = values.sum()
    if (total == 0f) return
    
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier.size(size)
        ) {
            drawPieChart(values, colors, total, strokeWidth.toPx())
        }
    }
}

private fun DrawScope.drawPieChart(
    values: List<Float>,
    colors: List<Color>,
    total: Float,
    strokeWidth: Float
) {
    var currentAngle = -90f // Start from top
    
    values.forEachIndexed { index, value ->
        val sweepAngle = (value / total) * 360f
        val color = colors[index % colors.size]
        
        drawArc(
            color = color,
            startAngle = currentAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        
        currentAngle += sweepAngle
    }
}

private val defaultPieColors = listOf(
    Color(0xFF10B981), // Emerald
    Color(0xFFEF4444), // Red
    Color(0xFFF59E0B), // Amber
    Color(0xFF6366F1), // Indigo
    Color(0xFF8B5CF6), // Violet
    Color(0xFFEC4899), // Pink
    Color(0xFF06B6D4), // Cyan
    Color(0xFF84CC16)  // Lime
)