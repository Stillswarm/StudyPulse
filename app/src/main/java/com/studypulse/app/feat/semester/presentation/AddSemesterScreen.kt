package com.studypulse.app.feat.semester.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.components.DateRangePickerModal
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.common.util.toFullString
import com.studypulse.app.common.util.toLocalDate
import com.studypulse.app.feat.semester.domain.model.SemesterName
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.GreenSecondary
import com.studypulse.app.ui.theme.LightGray
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSemesterScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    vm: AddSemesterScreenViewModel = koinViewModel()
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


    Box(modifier = modifier.fillMaxSize()) {
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp))
                        .background(LightGray)
                        .animateContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable { yearMenuExpanded = !yearMenuExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (state.year == null) "Select Year"
                                else "Year: ${state.year}",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(16.dp)
                            )

                            Icon(
                                painter = painterResource(if (yearMenuExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                                contentDescription = "Expand",
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = yearMenuExpanded,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGray)
                                .clip(RoundedCornerShape(0, 0, 0, 8))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                (1..5).forEach { y ->
                                    Text(
                                        y.toString(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .noRippleClickable {
                                                vm.updateYear(y)
                                                yearMenuExpanded = false
                                            },
                                        textAlign = TextAlign.Center
                                    )
                                    if (y < 5) HorizontalDivider()
                                }
                            }
                        }
                    }
                }

                // select semester name
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp))
                        .background(LightGray)
                        .animateContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .noRippleClickable { semNameMenuExpanded = !semNameMenuExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (state.name == null) "Select Semester"
                                else "Semester: ${state.name?.name?.convertToSentenceCase()}",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(16.dp)
                            )

                            Icon(
                                painter = painterResource(if (semNameMenuExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down),
                                contentDescription = "Expand",
                                modifier = Modifier
                                    .padding(8.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = semNameMenuExpanded,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightGray)
                                .clip(RoundedCornerShape(0, 0, 0, 8))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SemesterName.entries.forEach { e ->
                                    Text(
                                        e.name.convertToSentenceCase(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .noRippleClickable {
                                                vm.updateName(e)
                                                semNameMenuExpanded = false
                                            },
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // min required attendance
                TextField(
                    value = state.minAttendance?.toString() ?: "",
                    onValueChange = { vm.updateMinAttendance(it.toInt()) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Min Required Attendance", fontSize = 16.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = LightGray.copy(alpha = 0.75f),
                        focusedContainerColor = LightGray
                    )
                )

                // select semester start - end range
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp))
                        .background(LightGray),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (state.startDate == null) "Select Semester Range"
                            else "${state.startDate?.toFullString()} - ${state.endDate?.toFullString()}",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = "Expand",
                            modifier = Modifier
                                .padding(8.dp)
                                .noRippleClickable { showDatePicker = !showDatePicker }
                        )
                    }
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
                            .noRippleClickable {
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
    }
}