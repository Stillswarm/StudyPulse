package com.studypulse.app.feat.attendance.courses.presentation.add_course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.ui.theme.GreenDark
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.LightGray
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddCourseScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: AddCourseViewModel = koinViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            AppTopBar(
                backgroundColor = GreenDark,
                foregroundGradient = null,
                title = "Add Course",
                titleColor = Color.White,
                navigationIcon = R.drawable.ic_arrow_left,
                onNavigationClick = onNavigateBack,
                actionIcon = null,
                onActionClick = null
            )

            Spacer(Modifier.height(24.dp))

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    state.activeSemester?.let { sem ->
                        TextField(
                            value = state.courseName,
                            onValueChange = { vm.onCourseNameChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = LightGray.copy(alpha = 0.75f),
                                focusedContainerColor = LightGray
                            ),
                            shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                            placeholder = { Text("Course Name") }
                        )

                        TextField(
                            value = state.courseCode,
                            onValueChange = { vm.onCourseCodeChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = LightGray.copy(alpha = 0.75f),
                                focusedContainerColor = LightGray
                            ),
                            shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                            placeholder = { Text("Course Code") }
                        )

                        TextField(
                            value = state.instructor,
                            onValueChange = { vm.onInstructorChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = LightGray.copy(alpha = 0.75f),
                                focusedContainerColor = LightGray
                            ),
                            shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                            placeholder = { Text("Course Instructor") }
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GreenLight)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("This course will be added to the current semester: ${sem.name}, year ${sem.year}\n")

                                    withStyle(
                                        SpanStyle(
                                            color = GreenDark,
                                            fontWeight = FontWeight.Medium,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    ) {
                                        append("Click here to change semester")
                                    }
                                },
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            onClick = { vm.onSubmit(onNavigateBack) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenDark,
                            ),
                            shape = RoundedCornerShape(8.dp, 0.dp, 8.dp, 0.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Text("Submit", color = Color.White, fontSize = 18.sp)
                        }
                    }

                    state.errorMsg?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}