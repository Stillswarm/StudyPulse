package com.studypulse.app.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeInputDialog(
    timePickerState: TimePickerState,
    onTimeChange: (Int, Int) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {

    TimeInput(
        state = timePickerState,
        modifier = Modifier.fillMaxWidth(),
        colors = TimePickerDefaults.colors(
            timeSelectorSelectedContainerColor = containerColor,
            timeSelectorUnselectedContainerColor = containerColor,
        )
    )
    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        onTimeChange(timePickerState.hour, timePickerState.minute)
    }
}