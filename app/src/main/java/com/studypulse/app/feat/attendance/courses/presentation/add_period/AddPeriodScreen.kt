package com.studypulse.app.feat.attendance.courses.presentation.add_period

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.components.TimeInputDialog
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.LightGray
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPeriodScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddPeriodScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.fillMaxSize()) {
        Column {
            AppTopBar(
                backgroundColor = GreenDark,
                foregroundGradient = null,
                title = "Add Period",
                titleColor = Color.White,
                navigationIcon = R.drawable.ic_arrow_left,
                onNavigationClick = onNavigateBack,
                actionIcon = null,
                onActionClick = null
            )

            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                        shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                        onClick = { expanded = !expanded },
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = state.selectedDay.name.convertToSentenceCase(),
                                    fontSize = 16.sp,
                                    color = Color.Black
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
                                Day.entries.forEachIndexed { index, day ->
                                    DropdownMenuItem(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        text = { Text(day.name.convertToSentenceCase(), fontSize = 16.sp) },
                                        onClick = {
                                            viewModel.onDayChange(day)
                                            expanded = false
                                        }
                                    )
                                    if (index < Day.entries.size - 1) {
                                        HorizontalDivider()
                                    }
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

                    val startTimePickerState = rememberTimePickerState(
                        initialHour = state.startTimeHour,
                        initialMinute = state.startTimeMinute,
                        is24Hour = true
                    )
                    TimeInputDialog(
                        timePickerState = startTimePickerState,
                        onTimeChange = { h, m ->
                            viewModel.onStartTimeChange(h, m)
                        },
                        containerColor = LightGray
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "End Time",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    val endTimePickerState = rememberTimePickerState(
                        initialHour = state.endTimeHour,
                        initialMinute = state.endTimeMinute,
                        is24Hour = true
                    )
                    TimeInputDialog(
                        timePickerState = endTimePickerState,
                        onTimeChange = { h, m ->
                            viewModel.onEndTimeChange(h, m)
                        },
                        containerColor = LightGray
                    )
                }

                Button(
                    onClick = {
                        viewModel.onSubmit(onNavigateBack)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
                ) {
                    Text(
                        text = "Add Period",
                        fontSize = 16.sp,
                        color = Color.White,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}