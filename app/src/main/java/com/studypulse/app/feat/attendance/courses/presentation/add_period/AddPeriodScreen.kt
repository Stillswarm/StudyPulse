package com.studypulse.app.feat.attendance.courses.presentation.add_period

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.components.TimeInputDialog
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.common.util.formatWithLeadingZero
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.GreenSecondary
import com.studypulse.app.ui.theme.LightGray
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPeriodScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: AddPeriodScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            AppTopBar(
                backgroundColor = GreenSecondary,
                foregroundGradient = null,
                title = if (state.periodId.isNotEmpty()) "Edit Period" else "Add Period",
                titleColor = Color.White,
                navigationIcon = R.drawable.ic_arrow_left,
                onNavigationClick = onNavigateBack,
                actionIcon = null,
                onActionClick = null
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Day",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    var expanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp))
                            .noRippleClickable { expanded = !expanded }
                            .background(Color.Transparent)
                            .padding(0.dp)
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

                            AnimatedVisibility(
                                visible = expanded,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(0, 0, 0, 8))
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Day.entries.forEachIndexed { idx, y ->
                                        Text(
                                            text = y.name.convertToSentenceCase(),
                                            fontSize = 14.sp,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .noRippleClickable {
                                                    vm.onDayChange(y)
                                                    expanded = false
                                                },
                                            textAlign = TextAlign.Center
                                        )
                                        if (idx < 6) HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Start Time (24-hr Clock)",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightGray)
                            .noRippleClickable { vm.showStartTimePicker() }
                    ) {
                        Text(
                            text = "${formatWithLeadingZero(state.startTimeHour)} : ${
                                formatWithLeadingZero(
                                    state.startTimeMinute
                                )
                            }",
                            color = DarkGray,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "End Time (24-hr Clock)",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightGray)
                            .noRippleClickable { vm.showEndTimePicker() }
                    ) {
                        Text(
                            text = "${formatWithLeadingZero(state.endTimeHour)} : ${
                                formatWithLeadingZero(
                                    state.endTimeMinute
                                )
                            }",
                            color = DarkGray,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        vm.onSubmit(onNavigateBack)
                    },
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSecondary)
                ) {
                    Text(
                        text = if (state.periodId.isNotEmpty()) "Update" else "Add Period",
                        fontSize = 16.sp,
                        color = Color.White,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        val startTimePickerState = rememberTimePickerState(
            initialHour = state.startTimeHour,
            initialMinute = state.startTimeMinute,
            is24Hour = true
        )
        if (state.showStartTimePicker) {
            TimeInputDialog(
                onDismissRequest = { vm.hideStartTimePicker() },
                timePickerState = startTimePickerState,
                onTimeChange = { h, m ->
                    vm.onStartTimeChange(h, m)
                },
                modifier = Modifier.align(Alignment.Center),
                containerColor = LightGray,
//                onMinuteChange = {
//                    vm.hideStartTimePicker()
//                    vm.showEndTimePicker()
//                }
            )
        }

        val endTimePickerState = rememberTimePickerState(
            initialHour = state.endTimeHour,
            initialMinute = state.endTimeMinute,
            is24Hour = true
        )
        if (state.showEndTimePicker) {
            TimeInputDialog(
                onDismissRequest = { vm.hideEndTimePicker() },
                timePickerState = endTimePickerState,
                onTimeChange = { h, m ->
                    vm.onEndTimeChange(h, m)
                },
                modifier = Modifier.align(Alignment.Center),
                containerColor = LightGray
            )
        }
        if (state.showConfirmationPopup) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { vm.updateShowConfirmationPopup(false) },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(12.dp, shape = RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenLight)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You have chosen a period that is ${state.timeRange} long. Classes aren't typically this long. Are you sure you want to proceed?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            vm.updateShowConfirmationPopup(false)   // hide popup
                            vm.updateGranted(true)
                            vm.onSubmit(onNavigateBack) // delete
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenDark
                        )
                    ) {
                        Text(text = "Continue anyway")
                    }

                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GreenDark
                        ),
                        border = BorderStroke(1.dp, GreenSecondary),
                        onClick = {
                            vm.updateShowConfirmationPopup(false)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(text = "Go back and change")
                    }
                }
            }
        }
    }
}