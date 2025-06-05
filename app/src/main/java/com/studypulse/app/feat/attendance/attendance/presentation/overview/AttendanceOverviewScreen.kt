package com.studypulse.app.feat.attendance.attendance.presentation.overview

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.common.ui.components.noRippleClickable
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRecord
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceStatus
import com.studypulse.app.feat.attendance.attendance.presentation.AttendanceStatsSharedViewModel
import com.studypulse.app.feat.attendance.courses.data.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceOverviewScreen(
    attendanceByCourse: Map<Long, List<AttendanceRecord>>,
    onDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AttendanceStatsSharedViewModel
) {

    val allCourses by viewModel.allCoursesMap.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Attendance Overview") }
            )
        }
    ) { innnerPadding ->
        Box(
            modifier = modifier
                .padding(innnerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(attendanceByCourse.keys.toList()) { courseId ->
                    allCourses[courseId]?.let { course ->
                        AttendanceOverviewItem(
                            attendanceRecords = attendanceByCourse[courseId] ?: emptyList(),
                            course = course,
                            onDetails = { onDetails(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceOverviewItem(
    attendanceRecords: List<AttendanceRecord>,
    course: Course,
    onDetails: (Long) -> Unit,
    modifier: Modifier = Modifier
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
    val percentPresent = remember(attendanceRecords) {
        if (totalClasses - cancelled > 0) (present.toFloat()) / (totalClasses - cancelled)
        else 0f
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = course.courseName,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = course.courseCode,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "${percentPresent * 100}%",
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    fontSize = 24.sp,
                    color = Color.Green
                )
            }

            LinearProgressIndicator(
                progress = {
                    percentPresent
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.Green,
                trackColor = Color.LightGray,
                strokeCap = StrokeCap.Round,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$present/${totalClasses - cancelled} Classes",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )

                Text(
                    text = "View Details >",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Blue,
                    modifier = Modifier.noRippleClickable { onDetails(course.id) }
                )
            }
        }
    }
}