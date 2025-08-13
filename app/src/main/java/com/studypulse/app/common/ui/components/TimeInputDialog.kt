package com.studypulse.app.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputDialog(
    onDismissRequest: () -> Unit,
    timePickerState: TimePickerState,
    onTimeChange: (Int, Int) -> Unit,
    containerColor: Color,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {

    Popup(
        onDismissRequest = { onDismissRequest() },
        alignment = Alignment.Center,
    ) {
        TimePicker(
            state = timePickerState,
            modifier = modifier.fillMaxWidth(),
            colors = TimePickerDefaults.colors(
                clockDialColor = containerColor,
                selectorColor = containerColor,
                periodSelectorSelectedContainerColor = primaryColor,
                timeSelectorSelectedContainerColor = primaryColor,
                timeSelectorUnselectedContainerColor = containerColor,
            ),
        )
    }
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeChange(timePickerState.hour, timePickerState.minute)
    }
}