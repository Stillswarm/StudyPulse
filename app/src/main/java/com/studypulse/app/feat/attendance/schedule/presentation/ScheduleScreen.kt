package com.studypulse.app.feat.attendance.schedule.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.common.util.getAbbreviatedName
import com.studypulse.app.common.util.to12HourString
import com.studypulse.app.feat.attendance.schedule.data.Day
import com.studypulse.app.feat.attendance.schedule.data.Period
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateToFullSchedule: () -> Unit,
    navigateToAddPeriod: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScheduleScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("My Schedule") }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LazyRow {
                    Day.entries.forEach { day ->
                        val current = state.currentDay == day
                        item {
                            Box(
                                modifier = Modifier
                                    .drawWithContent {
                                        drawContent()
                                        drawLine(
                                            color = if (current) Color.Blue else Color.Gray,
                                            start = Offset(0f, size.height),
                                            end = Offset(size.width, size.height),
                                            strokeWidth = if (current) 2.dp.toPx() else 0.5.dp.toPx()
                                        )
                                    }
                                    .clickable { viewModel.toggleDay(day) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.name.convertToSentenceCase().getAbbreviatedName(),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = if (current) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (current) Color.Blue else Color.Black,
                                    modifier = Modifier.padding(
                                        horizontal = 24.dp,
                                        vertical = 17.dp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (state.schedule.isEmpty()) {
                    Text(
                        text = "No schedule yet!",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(state.schedule) {
                            ScheduleItem(
                                period = it,
                            )
                        }
                    }
                }

                HorizontalDivider()

                state.courseId?.let {
                    OutlinedButton(
                        onClick = {
                            navigateToAddPeriod(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Add Period")
                    }

                    TextButton(
                        onClick = onNavigateToFullSchedule,
                    ) {
                        Text(
                            text = "View Full Schedule",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(
    period: Period,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable { }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = period.courseName,
                    fontSize = 18.sp,
                    lineHeight = 28.sp,
                )

                Text(
                    text = "${period.startTime.to12HourString()} - ${period.endTime.to12HourString()}",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.DarkGray
                )
            }

            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Expand",
                tint = Color.DarkGray
            )
        }
    }
}