package com.studypulse.app.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun DefaultHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationButton: (@Composable () -> Unit)? = null,
    headingSize: HeadingSize = HeadingSize.Subtitle1,
    headingAlign: HeadingAlign = HeadingAlign.Start,
    backgroundColor: Color = Color.White,
    headerElevation: Dp = 0.dp,
    shadowAlpha: Float = 0.02f,
    actionButtons: (@Composable() (RowScope.() -> Unit))? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .zIndex(1f)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationButton?.let {
            Row(horizontalArrangement = Arrangement.Start) {
                it.invoke()
                Spacer(Modifier.padding(start = 16.dp))
            }
        }
        Text(
            modifier = Modifier
                .basicMarquee(velocity = 60.dp)
                .weight(1f),
            textAlign = headingAlign.textAlign,
            text = title ?: "",
            maxLines = 1,
            overflow = TextOverflow.Clip,
            style = headingSize.textStyle,
            color = Color.Gray
        )
        actionButtons?.let {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.End
                )
            ) {
                Spacer(Modifier.padding(start = 2.dp))
                actionButtons()
            }
        }
    }
}


enum class HeadingSize {
    H5, Subtitle1;

    val textStyle: TextStyle
        @Composable
        get() = when (this) {
            H5 -> MaterialTheme.typography.headlineLarge
            Subtitle1 -> MaterialTheme.typography.titleLarge
        }
}

enum class HeadingAlign {
    Center, Start;

    val textAlign: TextAlign
        @Composable
        get() = when (this) {
            Center -> TextAlign.Center
            Start -> TextAlign.Start
        }
}