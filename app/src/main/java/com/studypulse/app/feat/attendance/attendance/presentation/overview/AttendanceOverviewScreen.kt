package com.studypulse.app.feat.attendance.attendance.presentation.overview

import android.R.attr.lineHeight
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.util.MathUtils
import com.studypulse.app.common.util.MathUtils.INF
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.GreenNormal
import com.studypulse.app.ui.theme.LightGray
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
    val semesterSummary by vm.semesterSummaryFlow.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
        .fillMaxSize()
        .testTag("AttendanceOverviewScreen_Root")) {
        Column(modifier = Modifier
            .fillMaxSize()
            .testTag("AttendanceOverviewScreen_MainColumn")) {
            val scope = rememberCoroutineScope()
            AppTopBar(
                backgroundColor = Gold,
                title = state.topBarTitle,
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
                modifier = Modifier.testTag("AttendanceOverviewScreen_AppTopBar")
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .testTag("AttendanceOverviewScreen_LazyColumn"),
                contentPadding = PaddingValues(16.dp)
            ) {
                semesterSummary?.let { s ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("AttendanceOverviewScreen_SummaryRow"),
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
                                    .testTag("AttendanceOverviewScreen_MinAttendanceBox")
                            ) {
                                Column {
                                    Text(
                                        text = "${s.minAttendance}%",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
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

                            var percentage by remember(s.presentRecords, s.absentRecords) {
                                mutableIntStateOf(
                                    MathUtils.calculatePercentage(
                                        s.presentRecords,
                                        s.totalClasses
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .border(1.dp, Gold.copy(0.5f), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                                    .weight(1f)
                                    .testTag("AttendanceOverviewScreen_CurrentAttendanceBox")
                            ) {
                                Column {
                                    Text(
                                        text = "${if (percentage == INF) "--" else percentage}%",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
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

                    items(state.courseWiseSummaries.toList(), key = { it.component1().id }) { mp ->
                        val c = mp.component1() // key = course
                        val cs = mp.component2()    // value = courseSummary
                        AttendanceOverviewItem(c, cs, onDetails, modifier = Modifier.testTag("AttendanceOverviewScreen_Item_${c.courseCode}"))
                    }
                }
            }
        }

        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier
                .align(Alignment.Center)
                .testTag("AttendanceOverviewScreen_LoadingIndicator"), color = DarkGray)
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
            .testTag("AttendanceOverviewScreen_ItemBox_${course.courseCode}")
//            .noRippleClickable { onDetails(course.id) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("AttendanceOverviewScreen_ItemColumn_${course.courseCode}"),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("AttendanceOverviewScreen_ItemRow_${course.courseCode}"),
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
                    modifier = Modifier.testTag("AttendanceOverviewScreen_PercentageContainer_${course.courseCode}")
                ) {
                    Text(
                        text = if (percentage == INF) "--" else percentage.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                    )

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("AttendanceOverviewScreen_PercentageBar_${course.courseCode}"),
                        color = if (percentage < summary.minAttendance) Red
                        else if (percentage <= 10 + summary.minAttendance) Gold
                        else GreenNormal,
                        progress = { if (percentage == INF) 0f else percentage.toFloat() / 100 },
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
                    text = if (percentage == INF)     // when no classes
                            "No classes yet"
                        else if (percentage < summary.minAttendance)
                                if (summary.minAttendance == 100)
                                    "Attend all classes"
                                else
                                    "Attend the next: ${
                                    MathUtils.minClassesRequired(
                                        summary.presentRecords,
                                        summary.absentRecords,
                                        summary.unmarkedRecords,
                                        summary.minAttendance
                                    )
                                } classes"
                        else
                            "Safe to skip: ${
                            MathUtils.maxSkipsAllowed(
                                summary.presentRecords,
                                summary.absentRecords,
                                summary.unmarkedRecords,
                                summary.minAttendance
                            )
                        } classes",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (percentage == INF) DarkGray.copy(0.6f) else if (percentage < summary.minAttendance) Red else GreenNormal
                )
            }
        }
    }
}