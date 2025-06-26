package com.studypulse.app.feat.attendance.courses.presentation.course_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.noRippleClickable
import org.koin.androidx.compose.koinViewModel

@Composable
fun CourseDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSchedule: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseDetailsScreenViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        state.course?.let { course ->
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = course.courseName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            lineHeight = 32.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${course.courseCode} • ${course.instructor}",
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calender),
                                    contentDescription = null,
                                    tint = Color.Blue,
                                )

                                Text(
                                    text = "View Course Schedule",
                                    color = Color.Blue,
                                    fontSize = 18.sp,
                                    letterSpacing = 0.75.sp,
                                    modifier = Modifier.noRippleClickable {
                                        onNavigateToSchedule(course.id)
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    HorizontalDivider()
                }
            }
        }
    }
}