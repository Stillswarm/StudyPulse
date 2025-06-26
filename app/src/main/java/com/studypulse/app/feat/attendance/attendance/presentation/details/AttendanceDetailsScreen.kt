package com.studypulse.app.feat.attendance.attendance.presentation.details

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.BrandPieChart
import com.studypulse.app.common.ui.components.DefaultHeader
import com.studypulse.app.common.ui.components.LinearProgressBar
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.presentation.AttendanceStatsSharedViewModel
import com.studypulse.app.feat.attendance.courses.domain.model.Course

@Composable
fun AttendanceDetailsScreen(
    course: Course,
    attendanceRecords: List<AttendanceRecord>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AttendanceStatsSharedViewModel
) {

    val cancelled = remember(attendanceRecords) {
        attendanceRecords.count { it.status == AttendanceStatus.CANCELLED }
    }
    val totalClasses = remember(attendanceRecords) {
        attendanceRecords.size
    }
    val present = remember(attendanceRecords) {
        attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
    }
    val percentPresentWithCancelled = remember(attendanceRecords) {
        if (totalClasses > 0) (present.toFloat()) / (totalClasses)
        else 0f
    }
    val percentPresent = remember(attendanceRecords) {
        if (totalClasses - cancelled > 0) (present.toFloat()) / (totalClasses - cancelled)
        else 0f
    }
    val percentCancelled = remember(attendanceRecords) {
        if (totalClasses > 0) (cancelled.toFloat()) / (totalClasses)
        else 0f
    }


    Box(
        modifier = modifier
            .background(Color.LightGray)
    ) {
        Column {
            DefaultHeader(
                title = "${course.courseCode} Attendance",
                navigationButton = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.clickable { onBack() }
                    )
                }
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // course header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = modifier.padding(horizontal = 16.dp)

                        ) {
                            Text(
                                text = course.courseName,
                                fontSize = 24.sp,
                                lineHeight = 32.sp,
                            )

                            Text(
                                text = course.courseCode,
                                fontSize = 14.sp,
                                lineHeight = 24.sp,
                            )
                        }
                    }
                }

                // attendance stats
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatWithSubtitle(stat = totalClasses, subtitle = "Classes")
                                StatWithSubtitle(stat = present, subtitle = "Present")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatWithSubtitle(
                                    stat = totalClasses - present - cancelled,
                                    subtitle = "Absent"
                                )
                                StatWithSubtitle(stat = cancelled, subtitle = "Cancelled")
                            }

                            Spacer(Modifier.height(48.dp))

                            BrandPieChart(
                                values = listOf(
                                    percentPresentWithCancelled,
                                    1 - percentPresentWithCancelled - percentCancelled,
                                    percentCancelled
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(Modifier.height(48.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    24.dp,
                                    alignment = Alignment.CenterHorizontally
                                )
                            ) {
                                AttendanceLegendTag(
                                    tag = "Present",
                                    indicatorColor = Color.Green
                                )
                                AttendanceLegendTag(
                                    tag = "Absent",
                                    indicatorColor = Color.Red
                                )
                                AttendanceLegendTag(
                                    tag = "Cancelled",
                                    indicatorColor = Color.Gray
                                )
                            }
                        }
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${percentPresent * 100}%",
                                    fontSize = 30.sp,
                                    lineHeight = 36.sp
                                )

                                Text(
                                    text = "Current Attendance",
                                    lineHeight = 24.sp,
                                    fontSize = 16.sp,
                                    color = Color(0xFF4B5563)
                                )
                            }

                            LinearProgressBar(
                                progress = percentPresent,
                                color = when (percentPresent) {
                                    in 0.0..0.4 -> Color(0xFFF87171)
                                    in 0.4..0.7 -> Color(0xFFFBBF24)
                                    else -> Color(0xFF34D399)
                                },
                            )
                        }
                    }
                }

                // history
                // todo: write this section
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clip(RoundedCornerShape(12.dp))
//                        .padding(horizontal = 16.dp)
//                        .background(Color.White),
//                ) {
//                    Column(
//                        verticalArrangement = Arrangement.spacedBy(16.dp),
//                    ) {
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically,
//                        ) {
//                            Text(
//                                text = "Class History",
//                                fontSize = 18.sp,
//                                lineHeight = 28.sp,
//                            )
//
//                            Text(
//                                text = "View All ->",
//                                fontSize = 14.sp,
//                                lineHeight = 20.sp,
//                                color = Color.Blue
//                            )
//                        }
//                    }
//                }
//            }
            }
        }
    }
}

@Composable
fun StatWithSubtitle(
    stat: Int,
    subtitle: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stat.toString(),
            fontSize = 24.sp,
            lineHeight = 32.sp,
        )

        Text(
            text = subtitle,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color(0xFF4B5563)
        )
    }
}

@Composable
fun AttendanceLegendTag(
    tag: String,
    indicatorColor: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_filled_circle),
            contentDescription = null,
            tint = indicatorColor,
        )

        Text(
            text = tag,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color(0xFF4B5563)
        )
    }
}