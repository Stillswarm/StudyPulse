package com.studypulse.app.feat.attendance.attendance.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AllSemestersBottomSheet
import com.studypulse.app.common.ui.components.LargeAppTopBar
import com.studypulse.app.common.ui.components.SemesterBottomSheetItem
import com.studypulse.app.common.ui.modifier.gradientFill
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.feat.attendance.calender.ui.components.AttendanceStatusButtonsRow
import com.studypulse.app.nav.OverviewType
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.GreenSecondary
import com.studypulse.app.ui.theme.Purple
import com.studypulse.app.ui.theme.WarmWhite
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    onNavigateToAddSemester: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCourseList: () -> Unit,
    onNavigateToAttendanceCalendar: () -> Unit,
    onNavigateToAttendanceOverview: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: AttendanceScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val semId by vm.semesterIdFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val semesterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)


    LaunchedEffect(semId) {
        if (semId.isNotBlank()) vm.fetchInitialData()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("AttendanceScreen_Root")
    ) {
        LargeAppTopBar(
            backgroundColor = Gold,
            title = "Your Ultimate Bunk Mate!",
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
            imageRes = R.drawable.im_books_black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .testTag("AttendanceScreen_TopBar")
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("AttendanceScreen_MainColumn")
        ) {
            Spacer(
                Modifier
                    .height(200.dp)
                    .testTag("AttendanceScreen_TopSpacer")
            ) // because top bar takes up 250.dp
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("AttendanceScreen_LoadingContainer"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Gold,
                        modifier = Modifier.testTag("AttendanceScreen_LoadingIndicator")
                    )
                }
            } else if (semId == "") {
                Text(
                    text = buildAnnotatedString {
                        append("No active semester found. You can pick or add an active semester ")

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = GreenSecondary,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("here")
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 50.dp)
                        .noRippleClickable { scope.launch { semesterSheetState.show() } }
                        .testTag("AttendanceScreen_NoSemesterText"),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("AttendanceScreen_LazyColumn"),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // stat box
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Gold),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("AttendanceScreen_QuickStatsCard")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .testTag("AttendanceScreen_QuickStatsColumn"),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Quick Stats",
                                    fontSize = 18.sp,
                                    lineHeight = 28.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.testTag("AttendanceScreen_QuickStats_Title")
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .testTag("AttendanceScreen_StatsInnerColumn")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("AttendanceScreen_StatsRow1"),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        StatBox(
                                            title = "Classes Unmarked",
                                            value = state.unmarkedCount,
                                            onClick = { onNavigateToAttendanceCalendar() },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("AttendanceScreen_Stat_Unmarked")
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        StatBox(
                                            title = "100% Attendance",
                                            value = state.fullAttendanceCount,
                                            onClick = { onNavigateToAttendanceOverview(OverviewType.FULL.name) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("AttendanceScreen_Stat_Full")
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("AttendanceScreen_StatsRow2"),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        StatBox(
                                            title = "Low Attendance",
                                            value = state.lowAttendanceCount,
                                            onClick = { onNavigateToAttendanceOverview(OverviewType.LOW.name) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("AttendanceScreen_Stat_LowAttendance")
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        StatBox(
                                            title = "Overall Percentage",
                                            value = state.attendancePercentage,
                                            onClick = { onNavigateToAttendanceOverview(OverviewType.ALL.name) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("AttendanceScreen_Stat_Percentage")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // empty course prompt
                    if (state.courseWiseSummaries.isEmpty()) {
                        item {
                            Text(
                                text = "You haven't added any courses yet. Click on \"Courses Overview\" to get started.",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }

                    // active semester
                    state.activeSemester?.let { sem ->
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Gold.copy(0.2f))
                            ) {
                                SemesterBottomSheetItem(
                                    selectedSemester = sem.id,
                                    semester = sem,
                                    onSemesterClick = {
                                        scope.launch { semesterSheetState.show() }
                                    },
                                    buttonColor = Gold,
                                    modifier = Modifier.testTag("AttendanceScreen_SemButton")
                                )
                            }
                        }
                    }

                    // attendance overview
                    item {
                        DashboardNavButton(
                            icon = R.drawable.ic_stats,
                            title = "Attendance Overview",
                            desc = "View detailed statistics",
                            onClick = { onNavigateToAttendanceOverview(OverviewType.ALL.name) },
                            modifier = Modifier.testTag("AttendanceScreen_AttendanceOverviewButton")
                        )
                    }

                    // courses overview
                    item {
                        DashboardNavButton(
                            icon = R.drawable.ic_book,
                            title = "Courses Overview",
                            desc = "Manage your courses",
                            onClick = onNavigateToCourseList,
                            modifier = Modifier.testTag("AttendanceScreen_CoursesOverviewButton")
                        )
                    }

                    // attendance calendar
                    item {
                        DashboardNavButton(
                            icon = R.drawable.ic_calender,
                            title = "Attendance Calendar",
                            desc = "Mark attendance by date",
                            onClick = onNavigateToAttendanceCalendar,
                            modifier = Modifier.testTag("AttendanceScreen_AttendanceCalendarButton")
                        )
                    }

                    item {
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }

        AllSemestersBottomSheet(
            sheetState = semesterSheetState,
            semesterList = state.semesterList,
            selectedSemesterId = state.activeSemester?.id ?: "",
            onSemesterClick = {
                vm.onChangeActiveSemester(it)
                scope.launch { semesterSheetState.hide() }
            },
            onAddSemester = onNavigateToAddSemester,
            onDismiss = { scope.launch { semesterSheetState.hide() } },
            buttonColor = Gold,
        )
    }
}

@Composable
fun StatBox(
    title: String,
    value: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(WarmWhite)
            .border(2.dp, Gold, RoundedCornerShape(8.dp))
            .noRippleClickable { onClick() }
    ) {
        Text(
            text = value.toString(),
            fontSize = 64.sp,
            lineHeight = 24.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .gradientFill(
                    Brush.linearGradient(
                        colorStops = arrayOf(
                            Pair(0.0f, Color.Black.copy(0.75f)),
                            Pair(0.99f, Gold)
                        )
                    )
                )
                .align(Alignment.BottomStart)
                .offset((-8).dp, (16).dp)
        )

        Text(
            text = title,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(4.dp)
                .align(Alignment.TopEnd),
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun VerticalGraphBar(
    courseName: String,
    height: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .height(150.dp)
                .width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(height)
                    .background(Gold)
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "${(height * 100).toInt()}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 4.dp)
                )
            }
        }

        Text(
            courseName
        )
    }
}

@Composable
fun DashboardNavButton(
    @DrawableRes icon: Int,
    title: String,
    desc: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Gold.copy(alpha = 0.1f))
            .noRippleClickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Gold),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight(525),
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                )

                Text(
                    text = desc,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = DarkGray
                )
            }
        }
    }
}