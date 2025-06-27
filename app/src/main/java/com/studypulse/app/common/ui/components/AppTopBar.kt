package com.studypulse.app.common.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.ui.theme.Purple

@Composable
fun AppTopBar(
    backgroundColor: Color,
    foregroundGradient: Brush,
    title: String,
    @DrawableRes navigationIcon: Int,
    onNavigationClick: () -> Unit,
    @DrawableRes actionIcon: Int,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp).padding(top = 4.dp).align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = navigationIcon),
                contentDescription = "nav icon",
                modifier = Modifier
                    .size(48.dp)
                    .noRippleClickable { onNavigationClick() }
                    // 1) Force an off-screen layer so blend modes only see this Icon+gradient
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    // 2) Draw the icon into that layer and then paint the gradient masked by its alpha
                    .drawWithCache {
                        onDrawWithContent {
                            // draw the icon (any tint; we're only keeping its alpha)
                            drawContent()
                            // draw your gradient, but only inside the alpha produced above
                            drawRect(
                                brush = foregroundGradient,
                                size = size,        // fill the full Icon bounds
                                blendMode = BlendMode.SrcIn  // mask the gradient to the icon’s alpha
                            )
                        }
                    },
                tint = Purple
            )

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                letterSpacing = (-0.01).sp
            )

            Icon(
                painter = painterResource(id = actionIcon),
                contentDescription = "nav icon",
                modifier = Modifier
                    .size(28.dp)
                    .noRippleClickable { onActionClick() }
                    // 1) Force an off-screen layer so blend modes only see this Icon+gradient
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    // 2) Draw the icon into that layer and then paint the gradient masked by its alpha
                    .drawWithCache {
                        onDrawWithContent {
                            // draw the icon (any tint; we're only keeping its alpha)
                            drawContent()
                            // draw your gradient, but only inside the alpha produced above
                            drawRect(
                                brush = foregroundGradient,
                                size = size,        // fill the full Icon bounds
                                blendMode = BlendMode.SrcIn  // mask the gradient to the icon’s alpha
                            )
                        }
                    },
                tint = Purple
            )
        }
    }
}