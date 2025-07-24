package com.studypulse.app.feat.attendance.courses.presentation.course

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.ui.theme.DarkGray
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.GreenSecondary
import com.studypulse.app.ui.theme.Red
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    onAddNewCourse: (String?) -> Unit,
    onCourseTimetable: (String) -> Unit,
    modifier: Modifier = Modifier,
    vm: CoursesScreenViewModel = koinViewModel(),
) {

    LaunchedEffect(Unit) {
        vm.loadCoursesData()
    }

    val state = vm.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize(),
    ) {

        if (state.value.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (state.value.courses.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 100.dp), // to account for the top bar
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notebook),
                    contentDescription = "Notebook Icon"
                )

                Text(
                    text = "No courses added yet",
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                Text(
                    text = "Tap + to add your first course",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.value.courses.forEach { course ->
                    item {
                        CourseItem(
                            onCourseDetails = onCourseTimetable,
                            onDelete = vm::deleteCourse,
                            course = course,
                            onEdit = { onAddNewCourse(it) }
                        )
                    }
                }

                item {
                    Text(
                        text = "Click on a course to view more actions.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }

        AppTopBar(
            backgroundColor = GreenSecondary,
            title = "Your Courses",
            titleColor = Color.White,
            navigationIcon = R.drawable.logo_pulse,
            onNavigationClick = { scope.launch { NavigationDrawerController.toggle() } },
            foregroundGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    Pair(0f, Gold),
                    Pair(25f, Color.White)
                )
            ),
            actionIcon = null,
            onActionClick = null,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // FAB to add new course
        FloatingActionButton(
            onClick = { onAddNewCourse(null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .size(64.dp),
            shape = RoundedCornerShape(16.dp, 0.dp, 16.dp, 0.dp),
            containerColor = GreenSecondary,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun CourseItem(
    onCourseDetails: (String) -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (String) -> Unit,
    course: Course,
    modifier: Modifier = Modifier,
) {
    var showPopup by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, GreenSecondary, RoundedCornerShape(8.dp))
            .noRippleClickable { expanded = true }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(17.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = course.courseName,
                        fontSize = 18.sp,
                        lineHeight = 28.sp,
                    )

                    Text(
                        text = course.courseCode,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.DarkGray
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = "Expand",
                    tint = Color.DarkGray
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GreenDark
                            ),
                            border = BorderStroke(1.dp, GreenSecondary),
                            onClick = { onCourseDetails(course.id) },
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                        ) {
                            Text(text = "Details")
                        }

                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GreenDark
                            ),
                            border = BorderStroke(1.dp, DarkGray),
                            onClick = { onEdit(course.id) }, modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                        ) {
                            Text(text = "Edit")
                        }

                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GreenDark
                            ),
                            border = BorderStroke(1.dp, Red),
                            onClick = { showPopup = true },
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                        ) {
                            Text(text = "Delete")
                        }
                    }
                }
            }
        }

        if (showPopup) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { showPopup = false },
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenLight)
                        .padding(12.dp)
                ) {
                    Text(text = "The course \"${course.courseName}\", associated periods, and attendance records will be deleted. Do you wish to proceed?")
                    Row {
                        OutlinedButton(
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = GreenDark
                            ),
                            border = BorderStroke(1.dp, GreenSecondary),
                            onClick = { showPopup = false },
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                showPopup = false   // hide popup
                                onDelete(course.id) // delete
                                expanded = false   // hide actions
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenDark
                            )
                        ) {
                            Text(text = "Delete")
                        }
                    }
                }
            }
        }

    }
}