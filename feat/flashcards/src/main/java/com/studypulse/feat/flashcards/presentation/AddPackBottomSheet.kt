package com.studypulse.feat.flashcards.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Blue
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.Gold
import com.studypulse.ui.theme.GreenSecondary
import com.studypulse.ui.theme.Orange
import com.studypulse.ui.theme.Pink
import com.studypulse.ui.theme.Purple
import com.studypulse.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPackBottomSheet(
    fcp: FlashcardPack,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onColorPick: (Color) -> Unit,
    onPublicToggle: (Boolean) -> Unit,
    onCreate: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val swatches = remember {
        listOf(Cyan, Purple, Gold, GreenSecondary, Pink, Orange, Blue, DarkGray)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "New flashcard pack",
                style = Typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
            )

            SheetTextField(
                value = fcp.title,
                onValueChange = onTitleChange,
                placeholder = "Title",
                singleLine = true,
            )

            SheetTextField(
                value = fcp.description ?: "",
                onValueChange = onDescriptionChange,
                placeholder = "Description (optional)",
                singleLine = false,
                minLines = 2,
            )

            Text(
                text = "Color",
                style = Typography.labelLarge,
                color = DarkGray,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                swatches.forEach { c ->
                    val selected = c == fcp.color
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (selected) 3.dp else 0.dp,
                                color = Cyan,
                                shape = CircleShape,
                            )
                            .noRippleClickable { onColorPick(c) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Public",
                        style = Typography.titleMedium,
                        color = DarkGray,
                    )
                    Text(
                        text = "Anyone can find and star this pack",
                        style = Typography.bodySmall,
                        color = DarkGray.copy(alpha = 0.6f),
                    )
                }
                Switch(
                    checked = fcp.isPublic,
                    onCheckedChange = onPublicToggle,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Cyan,
                        checkedThumbColor = Color.White,
                    ),
                )
            }

            Button(
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                enabled = fcp.title.isNotBlank(),
            ) {
                Text(
                    text = "Create pack",
                    style = Typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun SheetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = Typography.bodyLarge,
                color = DarkGray.copy(alpha = 0.5f),
            )
        },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Cyan.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
            ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
    )
}