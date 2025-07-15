package com.studypulse.app.feat.attendance.attendance.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.modifier.gradientFill
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.feat.attendance.calender.ui.components.AttendanceStatusButtonsRow
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
    vm: AttendanceScreenViewModel = koinViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val semId by vm.semesterIdFlow.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            AppTopBar(
                backgroundColor = Gold,
                title = "Track Attendance",
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
                    modifier = Modifier.fillMaxWidth().padding(16.dp).noRippleClickable { onNavigateToAddSemester() },
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Quick Stats",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.01).sp,
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    StatBox(
                                        title = "Classes Unmarked",
                                        value = 21,
                                        onClick = onNavigateToAttendanceCalendar,
//                                        modifier = Modifier.weight(1f)
                                    )
                                    StatBox(
                                        title = "100% Attendance",
                                        value = 1,
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
                                        value = 2,
                                        onClick = onNavigateToAttendanceOverview,
                                    )
                                    StatBox(
                                        title = "Overall Percentage",
                                        value = 75,
                                        onClick = onNavigateToCourseList,
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                text = "Quick Attendance",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.01).sp,
                            )

                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(5, key = { it }) {
                                    QuickAttendanceBox(courseCode = "Course $it")
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                text = "Report",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.01).sp,
                            )

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    alignment = Alignment.CenterHorizontally
                                )
                            ) {
                                items(5) {
                                    VerticalGraphBar(height = Math.random().toFloat())
                                }
                            }
                        }
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
    modifier: Modifier = Modifier
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
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset((-8).dp, (16).dp)
                .gradientFill(
                    Brush.linearGradient(
                        colorStops = arrayOf(
                            Pair(0f, Purple),
                            Pair(0.99f, Gold.copy(alpha = 0.75f))
                        )
                    )
                )
        )

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.05).sp,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(4.dp)
                .align(Alignment.TopEnd),
            minLines = 2,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuickAttendanceBox(
    courseCode: String,
    modifier: Modifier = Modifier
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
    height: Float,
    modifier: Modifier = Modifier
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
                    .align(Alignment.BottomCenter))
//            ) {
//                Text(
//                    text = "${(height * 100).toInt()}",
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .padding(top = 4.dp)
//                )
//            }
        }

        Text(
            text = "MA-2001",
        )
    }
}
