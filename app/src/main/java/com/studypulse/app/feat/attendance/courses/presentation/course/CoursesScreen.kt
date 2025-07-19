package com.studypulse.app.feat.attendance.courses.presentation.course

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.NavigationDrawerController
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import com.studypulse.app.ui.theme.Gold
import com.studypulse.app.ui.theme.GreenDark
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    onAddNewCourse: () -> Unit,
    onCourseDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoursesScreenViewModel = koinViewModel(),
) {

    LaunchedEffect(Unit) {
        viewModel.loadCoursesData()
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 100.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.value.courses.forEach { course ->
                    CourseItem(onCourseDetails, course)
                    Log.d("CourseItem", "Course: ${course.id}")
                }
            }
        }

        AppTopBar(
            backgroundColor = GreenDark,
            title = "Add Course",
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

        FloatingActionButton(
            onClick = { onAddNewCourse() },
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(32.dp).size(64.dp),
            shape = RoundedCornerShape(16.dp, 0.dp, 16.dp, 0.dp),
            containerColor = GreenDark,
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
    course: Course,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, GreenDark, RoundedCornerShape(8.dp))
            .noRippleClickable { onCourseDetails(course.id) }
    ) {
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
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "Expand",
                tint = Color.DarkGray
            )
        }
    }
}