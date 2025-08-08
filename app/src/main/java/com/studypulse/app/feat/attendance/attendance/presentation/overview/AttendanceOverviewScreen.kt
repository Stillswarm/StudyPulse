package com.studypulse.app.feat.attendance.attendance.presentation.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.util.MathUtils
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.GreenNormal
import com.studypulse.app.ui.theme.Purple
import com.studypulse.app.ui.theme.Red
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceOverviewScreen(
    onNavigateToProfile: () -> Unit,
    onDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: AttendanceOverviewScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val courseWiseSummary by vm.courseWiseSummaryFlow.collectAsStateWithLifecycle()
    val semesterSummary by vm.semesterSummaryFlow.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            val scope = rememberCoroutineScope()
            AppTopBar(
                backgroundColor = Gold,
                title = "Attendance Overview",
                navigationIcon = R.drawable.logo_pulse,
                onNavigationClick = { scope.launch { NavigationDrawerController.toggle() } },
                actionIcon = R.drawable.ic_profile,
                onActionClick = onNavigateToProfile,
                foregroundGradient = Brush.linearGradient(
                    colorStops = arrayOf(
                        Pair(0f, Purple),
                        Pair(59f, Color.Black)
                    )
                ),
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                semesterSummary?.let { s ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Gold.copy(0.5f), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                                    .weight(1f)
                            ) {
                                Column {
                                    Text(
                                        text = "${s.minAttendance}%",
                                        fontSize = 36.sp,
                                        fontWeight = Bold,
                                        modifier = Modifier.padding(16.dp),
                                        lineHeight = 40.sp,
                                    )

                                    Text(
                                        text = "Required Attendance",
                                        lineHeight = 20.sp,
                                        fontSize = 14.sp,
                                        color = DarkGray,
                                        minLines = 2,
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Gold.copy(0.5f), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                                    .weight(1f)
                            ) {
                                Column {
                                    Text(
                                        text = "${MathUtils.calculatePercentage(
                                            s.presentRecords,
                                            s.totalClasses
                                        )}%",
                                        fontSize = 36.sp,
                                        fontWeight = Bold,
                                        modifier = Modifier.padding(16.dp),
                                        lineHeight = 40.sp,
                                    )

                                    Text(
                                        text = "Current Attendance", lineHeight = 20.sp,
                                        fontSize = 14.sp,
                                        color = DarkGray,
                                        minLines = 2,
                                    )
                                }
                            }

                        }
                    }

                    items(courseWiseSummary.entries.toList()) { mp ->
                        val c = mp.key
                        val cs = mp.value
                        AttendanceOverviewItem(c, cs, onDetails)
                    }
                }


            }
        }

        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = DarkGray)
        }
    }
}

@Composable
fun AttendanceOverviewItem(
    course: Course,
    summary: CourseSummary,
    onDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var percentage by remember(summary.presentRecords, summary.absentRecords) {
        mutableIntStateOf(
            MathUtils.calculatePercentage(
                summary.presentRecords,
                summary.totalClasses
            )
        )
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
//            .noRippleClickable { onDetails(course.id) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = course.courseCode,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )

                    Text(
                        text = course.courseName,
                        lineHeight = 20.sp,
                        fontSize = 14.sp,
                        color = DarkGray
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = percentage.toString(),
                        fontSize = 18.sp,
                        fontWeight = Bold,
                        lineHeight = 28.sp,
                    )

                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = if (percentage < summary.minAttendance) Red
                        else if (percentage <= 10 + summary.minAttendance) Gold
                        else GreenNormal,
                        progress = { percentage.toFloat() / 100 },
                        strokeWidth = 4.dp,
                        trackColor = Color.Transparent
                    )
                }
            }

            Column {
                Text(
                    text = "Classes Attended: ${summary.presentRecords}/${summary.totalClasses}",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = DarkGray
                )

                Text(
                    text = if (percentage < summary.minAttendance) "Attendance below threshold" else "Safe to skip: ${
                        MathUtils.maxSkipsAllowed(
                            summary.presentRecords,
                            summary.absentRecords,
                            summary.unmarkedRecords,
                            summary.minAttendance
                        )
                    } classes",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (percentage < summary.minAttendance) Red else GreenNormal
                )
            }
        }
    }
}