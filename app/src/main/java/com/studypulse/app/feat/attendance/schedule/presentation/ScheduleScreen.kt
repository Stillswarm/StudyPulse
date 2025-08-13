package com.studypulse.app.feat.attendance.schedule.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.common.util.convertToSentenceCase
import com.studypulse.app.common.util.getAbbreviatedName
import com.studypulse.app.common.util.to12HourString
import com.studypulse.app.feat.attendance.courses.domain.model.Day
import com.studypulse.app.feat.attendance.courses.domain.model.Period
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.GreenSecondary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onNavigateBack: () -> Unit,
    onNavigateToFullSchedule: () -> Unit,
    navigateToAddPeriod: (String, String?, Day) -> Unit,
    modifier: Modifier = Modifier,
    vm: ScheduleScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTopBar(
                backgroundColor = GreenSecondary,
                foregroundGradient = null,
                title = if (state.courseCode == null) "Class Timetable" else state.courseCode!!,
                navigationIcon = R.drawable.ic_arrow_left,
                onNavigationClick = { onNavigateBack() },
                actionIcon = null,
                onActionClick = null,
                titleColor = Color.White,
            )
            LazyRow {
                Day.entries.forEach { day ->
                    val current = state.currentDay == day
                    item {
                        Box(
                            modifier = Modifier
                                .drawWithContent {
                                    drawContent()
                                    drawLine(
                                        color = if (current) GreenSecondary else Color.Gray,
                                        start = Offset(0f, size.height),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = if (current) 4.dp.toPx() else 0.5.dp.toPx()
                                    )
                                }
                                .clickable { vm.toggleDay(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.name.convertToSentenceCase().getAbbreviatedName(),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                fontWeight = if (current) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (current) GreenSecondary else Color.Black,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 28.sp,
                    color = DarkGray
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.schedule) {
                        ScheduleItem(
                            period = it,
                            onEdit = {
                                if (state.courseId != null) navigateToAddPeriod(
                                    state.courseId!!,
                                    it.id,
                                    it.day
                                )
                            },
                            onDelete = {
                                vm.updatePeriodIdToDelete(it.id)
                                vm.updateShowDeleteDialog(true)
                            }
                        )
                    }
                }
                HorizontalDivider(Modifier.padding(16.dp))
            }

            state.courseId?.let {
                OutlinedButton(
                    onClick = {
                        navigateToAddPeriod(it, null, state.currentDay)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GreenSecondary,
                    ),
                    border = BorderStroke(1.dp, GreenSecondary)
                ) {
                    Text(text = "Add New Period", modifier = Modifier.padding(8.dp))
                }

                TextButton(
                    onClick = onNavigateToFullSchedule,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = GreenSecondary
                    )
                ) {
                    Text(
                        text = "View Full Schedule",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (state.showDeleteDialog && state.periodIdToDelete != null) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { vm.updateShowDeleteDialog(false) },
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
                        text = "Are you sure you want to delete this period? All associated attendance data will be lost",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            vm.updateShowDeleteDialog(false)   // hide popup
                            vm.deletePeriod(state.periodIdToDelete!!) // delete
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenDark
                        )
                    ) {
                        Text(text = "Delete")
                    }

                    OutlinedButton(
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GreenDark
                        ),
                        border = BorderStroke(1.dp, GreenSecondary),
                        onClick = {
                            vm.updateShowDeleteDialog(false)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(
    period: Period,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Edit,
//                    contentDescription = null,
//                    tint = Color.Gray,
//                    modifier = Modifier
//                        .noRippleClickable { onEdit() }
//                )

                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier
                        .noRippleClickable {
                            onDelete()
                        }
                )
            }
        }
    }
}