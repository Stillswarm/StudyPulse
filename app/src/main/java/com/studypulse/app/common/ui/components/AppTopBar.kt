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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTopBar(
    backgroundColor: Color,
    foregroundGradient: Brush?,
    title: String,
    @DrawableRes navigationIcon: Int,
    onNavigationClick: () -> Unit,
    @DrawableRes actionIcon: Int?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.Unspecified,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = navigationIcon),
                contentDescription = "nav icon",
                modifier = Modifier
                    .size(48.dp)
                    .noRippleClickable { onNavigationClick() }
                    .gradientFill(foregroundGradient),
                tint = Color.Unspecified
            )

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                letterSpacing = (-0.01).sp,
                color = titleColor,
            )

            if (actionIcon != null && onActionClick != null) {
                Icon(
                    painter = painterResource(id = actionIcon),
                    contentDescription = "nav icon",
                    modifier = Modifier
                        .size(28.dp)
                        .noRippleClickable { onActionClick() }
                        .gradientFill(foregroundGradient),
                    tint = Color.Unspecified
                )
            } else {
                Box(modifier = Modifier.size(48.dp))
            }
        }
    }
}