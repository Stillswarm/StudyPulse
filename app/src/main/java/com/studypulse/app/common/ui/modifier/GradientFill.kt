package com.studypulse.app.common.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.gradientFill(
    gradient: Brush?,
) =
    this.then(
        Modifier
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (gradient != null) {
                        drawRect(
                            brush = gradient,
                            size = size,
                            blendMode = BlendMode.SrcIn
                        )
                    }
                }
            }
    )