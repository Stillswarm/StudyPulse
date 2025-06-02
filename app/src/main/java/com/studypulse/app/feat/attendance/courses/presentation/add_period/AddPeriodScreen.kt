package com.studypulse.app.feat.attendance.courses.presentation.add_period

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.common.ui.components.noRippleClickable
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.feat.attendance.schedule.data.Day
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPeriodScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddPeriodScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Course") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .noRippleClickable { onNavigateBack() }
                    )
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Day",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    var expanded by remember { mutableStateOf(false) }
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        shape = RectangleShape,
                        onClick = { expanded = !expanded }
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = state.selectedDay.name.convertToSentenceCase(),
                                )

                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Drop Down"
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
                                Day.entries.forEach { day ->
                                    DropdownMenuItem(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = { Text(day.name.convertToSentenceCase()) },
                                        onClick = {
                                            viewModel.onDayChange(day)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Start Time",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    
                    val timePickerState = rememberTimePickerState(
                        initialHour = state.startTimeHour,
                        initialMinute = state.startTimeHour,
                        is24Hour = true,
                    )
                    TimeInput(
                        state = timePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
                        viewModel.onStartTimeChange(timePickerState.hour, timePickerState.minute)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "End Time",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    val timePickerState = rememberTimePickerState(
                        initialHour = state.endTimeHour,
                        initialMinute = state.endTimeMinute,
                        is24Hour = true
                    )
                    TimeInput(
                        state = timePickerState,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
                        viewModel.onEndTimeChange(timePickerState.hour, timePickerState.minute)
                    }
                }

                Button(
                    onClick = {
                        viewModel.onSubmit()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                ) {
                    Text(
                        text = "Add Period",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}