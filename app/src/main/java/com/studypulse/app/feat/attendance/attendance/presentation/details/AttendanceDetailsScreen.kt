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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.presentation.AttendanceStatsSharedViewModel
import com.studypulse.app.feat.attendance.courses.data.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDetailsScreen(
    course: Course,
    attendanceRecords: List<AttendanceRecord>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AttendanceStatsSharedViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Course Details") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = "Go Back",
                        modifier = Modifier.clickable { onBack() }
                    )
                }
            )
        }
    ) { innerPadding ->

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
                .padding(innerPadding)
                .background(Color(0xFF4B5563))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatWithSubtitle(stat = totalClasses, subtitle = "Total Classes")
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

                            Spacer(Modifier.height(24.dp))

                            BrandPieChart(
                                values = listOf(
                                    percentPresentWithCancelled,
                                    1 - percentPresent - percentCancelled,
                                    percentCancelled
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    24.dp,
                                    alignment = Alignment.CenterHorizontally
                                )
                            ) {
                                AttendanceLegendTag()
                                AttendanceLegendTag()
                                AttendanceLegendTag()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatWithSubtitle(
    stat: Int,
    subtitle: String
) {
    Column {
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
fun AttendanceLegendTag(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_filled_circle),
            contentDescription = null,
        )

        Text(
            text = "Attended",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color(0xFF4B5563)
        )
    }
}