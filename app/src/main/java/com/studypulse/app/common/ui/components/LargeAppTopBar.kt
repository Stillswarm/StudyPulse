package com.studypulse.app.common.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.common.ui.modifier.gradientFill
import com.studypulse.app.common.ui.modifier.noRippleClickable

@Composable
fun LargeAppTopBar(
    backgroundColor: Color,
    foregroundGradient: Brush?,
    title: String,
    @DrawableRes navigationIcon: Int,
    onNavigationClick: () -> Unit,
    @DrawableRes actionIcon: Int?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    titleColor: Color = Color.Unspecified,
    @DrawableRes imageRes: Int? = null,
) {
    SetStatusBarColor(backgroundColor)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(0.dp, 0.dp, 32.dp, 32.dp))
            .background(backgroundColor)
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = navigationIcon),
                    contentDescription = "nav icon",
                    modifier = Modifier
                        .size(36.dp)
                        .noRippleClickable { onNavigationClick() }
                        .gradientFill(foregroundGradient),
                    tint = titleColor
                )

//                Text(
//                    text = title,
//                    fontWeight = FontWeight.SemiBold,
//                    fontSize = 20.sp,
//                    letterSpacing = (-0.01).sp,
//                    color = titleColor,
//                )

                if (actionIcon != null && onActionClick != null) {
                    Icon(
                        painter = painterResource(id = actionIcon),
                        contentDescription = "nav icon",
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("large_top_bar_action_icon")
                            .noRippleClickable { onActionClick() }
                            .gradientFill(foregroundGradient),
                        tint = Color.Unspecified
                    )
                } else {
                    Box(modifier = Modifier.size(36.dp))
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterHorizontally)
                ) {
                    imageRes?.let {
                        Image(
                            modifier = Modifier.size(140.dp),
                            painter = painterResource(id = imageRes),
                            contentDescription = null,
                        )
                    }

                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(200.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}