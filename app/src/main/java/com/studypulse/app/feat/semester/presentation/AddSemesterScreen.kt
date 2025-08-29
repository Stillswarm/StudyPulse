package com.studypulse.app.feat.semester.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.components.DateRangePickerModal
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.common.util.toFullString
import com.studypulse.app.common.util.toLocalDate
import com.studypulse.app.feat.semester.domain.model.SemesterName
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.GreenSecondary
import com.studypulse.app.ui.theme.LightGray
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSemesterScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    vm: AddSemesterScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val dateRangePickerState = rememberDateRangePickerState()
    var yearMenuExpanded by remember {
        mutableStateOf(false)
    }
    var semNameMenuExpanded by remember {
        mutableStateOf(false)
    }
    var showDatePicker by remember {
        mutableStateOf(false)
    }


    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    focusManager.clearFocus()
                }
            )) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AppTopBar(
                backgroundColor = GreenSecondary,
                foregroundGradient = null,
                title = "Change Active Semester",
                titleColor = Color.White,
                navigationIcon = R.drawable.ic_arrow_left,
                onNavigationClick = onNavigateBack,
                actionIcon = null,
                onActionClick = null
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // select semester year
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Year",
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
                                    text = state.year?.toString() ?: "Select",
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
                                    (1..5).forEach { idx ->
                                        Text(
                                            text = idx.toString(),
                                            fontSize = 14.sp,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .noRippleClickable {
                                                    vm.updateYear(idx)
                                                    expanded = false
                                                },
                                            textAlign = TextAlign.Center
                                        )
                                        if (idx < 5) HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }

                // select semester name
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Semester",
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
                                    text = state.name?.name?.convertToSentenceCase() ?: "Select",
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
                                    SemesterName.entries.forEachIndexed { idx, y ->
                                        Text(
                                            text = y.name.convertToSentenceCase(),
                                            fontSize = 14.sp,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .noRippleClickable {
                                                    vm.updateName(y)
                                                    expanded = false
                                                },
                                            textAlign = TextAlign.Center
                                        )
                                        if (idx < 2) HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }

                // select semester start - end range
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Semester Range",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp))
                            .noRippleClickable { showDatePicker = !showDatePicker },
                        contentAlignment = Alignment.Center
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (state.startDate == null) "Select "
                                else "${state.startDate?.toFullString()} - ${state.endDate?.toFullString()}",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(16.dp)
                            )

                            Icon(
                                painter = painterResource(R.drawable.ic_calendar),
                                contentDescription = "Expand",
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                // min required attendance
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Min. Required Attendance",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextField(
                        value = state.minAttendance?.toString() ?: "",
                        maxLines = 1,
                        onValueChange = { vm.updateMinAttendance(if (it.isEmpty()) null else it.toInt()) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = LightGray.copy(alpha = 0.25f),
                            focusedContainerColor = LightGray.copy(0.75f)
                        )
                    )
                }

                // info box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenLight)
                ) {
                    Text(
                        text = "This semester will become your CURRENT semester, after adding.",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // errors, if any
                state.errorMsg?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // submit button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp, 0.dp, 8.dp, 0.dp))
                        .background(GreenSecondary)
                ) {
                    Text(
                        text = "Submit",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .noRippleClickable(enabled = !state.isLoading) {
                                vm.submit(onNavigateBack)
                            },
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (showDatePicker) {
            DateRangePickerModal(
                dateRangePickerState = dateRangePickerState,
                onDateRangeSelected = { p ->
                    p.first?.let { vm.updateStartDate(it.toLocalDate()) }
                    p.second?.let { vm.updateEndDate(it.toLocalDate()) }
                },
                onDismiss = { showDatePicker = false }
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
                        text = "You have chosen a semester that is ${state.dateRange} long. Semesters aren't typically this long. Are you sure you want to proceed?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            vm.updateShowConfirmationPopup(false)   // hide popup
                            vm.updateGranted(true)
                            vm.submit(onNavigateBack) // delete
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