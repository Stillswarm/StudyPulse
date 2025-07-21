package com.studypulse.app.common.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun OrDivider(
    modifier: Modifier = Modifier,
    text: String = " or ",
    lineColor: Color = Color.Gray,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    lineThickness: Dp = 1.dp,
    horizontalPadding: Dp = 8.dp
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            color = lineColor,
            thickness = lineThickness,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = text,
            style = textStyle,
            modifier = Modifier.padding(horizontal = horizontalPadding),
            color = lineColor
        )
        HorizontalDivider(
            color = lineColor,
            thickness = lineThickness,
            modifier = Modifier.weight(1f)
        )
    }
}