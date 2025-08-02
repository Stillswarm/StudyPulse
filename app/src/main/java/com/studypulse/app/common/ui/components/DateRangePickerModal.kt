package com.studypulse.app.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.GreenNormal
import com.studypulse.app.ui.theme.GreenSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    dateRangePickerState: DateRangePickerState,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
    containerColor: Color = GreenLight,
) {

    DatePickerDialog(
        shape = RoundedCornerShape(8.dp),
        colors = DatePickerDefaults.colors(
            containerColor = containerColor,
        ),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK", color = DarkGray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DarkGray)
            }
        }
    ) {
        DateRangePicker(
            colors = DatePickerDefaults.colors(
                containerColor = containerColor,
                selectedDayContainerColor = GreenDark,
                dayInSelectionRangeContainerColor = GreenNormal,
                todayDateBorderColor = GreenSecondary
            ),
            state = dateRangePickerState,
            title = { Text("Select Date Range") },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
//                .height(500.dp)
                .padding(8.dp)
        )
    }
}