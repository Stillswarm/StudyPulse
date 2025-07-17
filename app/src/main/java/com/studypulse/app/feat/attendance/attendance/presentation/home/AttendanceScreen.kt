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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.studypulse.app.common.ui.components.LargeAppTopBar
import com.studypulse.app.common.ui.modifier.gradientFill
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.feat.attendance.calender.ui.components.AttendanceStatusButtonsRow
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.Purple
import com.studypulse.app.ui.theme.WarmWhite
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AttendanceScreen(
    onNavigateToAddSemester: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCourseList: () -> Unit,
    onNavigateToAttendanceCalendar: () -> Unit,
    onNavigateToAttendanceOverview: () -> Unit,
    modifier: Modifier = Modifier,
    vm: AttendanceScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val semId by vm.semesterIdFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(semId) {
        if (semId.isNotBlank()) vm.fetchStatBoxData()
    }

    Box(
        modifier = modifier.fillMaxSize()
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
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(200.dp)) // because top bar takes up 250.dp
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenDark)
                }
            } else if (semId == "") {
                Text(
                    text = buildAnnotatedString {
                        append("No active semester found. You can add a new semester by clicking")

                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = GreenDark,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append(" here")
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 50.dp)
                        .noRippleClickable { onNavigateToAddSemester() },
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Gold),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Quick Stats",
                                    fontSize = 18.sp,
                                    lineHeight = 28.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        StatBox(
                                            title = "Classes Unmarked",
                                            value = state.unmarkedCount,
                                            onClick = onNavigateToAttendanceCalendar,
//                                        modifier = Modifier.weight(1f)
                                        )
                                        StatBox(
                                            title = "100% Attendance",
                                            value = state.fullAttendanceCount,
                                            onClick = onNavigateToAttendanceOverview,
//                                        modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        StatBox(
                                            title = "Low Attendance",
                                            value = state.lowAttendanceCount,
                                            onClick = onNavigateToAttendanceOverview,
                                        )
                                        StatBox(
                                            title = "Overall Percentage",
                                            value = state.attendancePercentage,
                                            onClick = onNavigateToCourseList,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        DashboardNavButton(
                            icon = R.drawable.ic_stats,
                            title = "Attendance Overview",
                            desc = "View detailed statistics",
                            onClick = onNavigateToAttendanceOverview,
                        )
                    }

                    item {
                        DashboardNavButton(
                            icon = R.drawable.ic_book,
                            title = "Courses Overview",
                            desc = "Manage your courses",
                            onClick = onNavigateToCourseList
                        )
                    }

                    item {
                        DashboardNavButton(
                            icon = R.drawable.ic_calender,
                            title = "Attendance Calendar",
                            desc = "Mark attendance by date",
                            onClick = onNavigateToAttendanceCalendar
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(80.dp)
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
fun QuickAttendanceBox(
    courseCode: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(WarmWhite)
            .border(1.dp, Gold, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "MA-2001",
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Thu, June 26",
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "9:00 - 12:30",
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            AttendanceStatusButtonsRow(
                attendanceRecord = null,
                onPresent = { },
                onAbsent = { },
                onCancelled = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
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
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
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