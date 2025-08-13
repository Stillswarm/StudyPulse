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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.app.R
import com.studypulse.app.common.ui.components.AllSemestersBottomSheet
import com.studypulse.app.common.ui.components.AppTopBar
import com.studypulse.app.common.ui.modifier.noRippleClickable
import com.studypulse.app.ui.theme.GreenLight
import com.studypulse.app.ui.theme.GreenNormal
import com.studypulse.app.ui.theme.GreenSecondary
import com.studypulse.app.ui.theme.LightGray
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddSemester: () -> Unit,
    modifier: Modifier = Modifier,
    vm: AddCourseScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val semSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                scope.launch { vm.loadAllSemesters() }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("AddCourseScreen_Root")) {
        Column {
            AppTopBar(
                backgroundColor = GreenSecondary,
                foregroundGradient = null,
                title = if (vm.courseId == null) "Add Course" else "Edit Course",
                titleColor = Color.White,
                navigationIcon = R.drawable.ic_arrow_left,
                onNavigationClick = onNavigateBack,
                actionIcon = null,
                onActionClick = null,
                modifier = Modifier.testTag("AddCourseScreen_TopBar")
            )

            Spacer(Modifier
                .height(24.dp)
                .testTag("AddCourseScreen_TopSpacer"))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("AddCourseScreen_FormColumn"),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                state.activeSemester?.let { sem ->
                    TextField(
                        value = state.courseName,
                        onValueChange = { vm.onCourseNameChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("AddCourseScreen_Input_CourseName"),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("AddCourseScreen_Input_CourseCode"),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("AddCourseScreen_Input_Instructor"),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = LightGray.copy(alpha = 0.75f),
                            focusedContainerColor = LightGray
                        ),
                        shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                        placeholder = { Text("Course Instructor") }
                    )

                    // min required attendance
                    TextField(
                        value = state.minAttendance,
                        onValueChange = { vm.updateMinAttendance(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("AddCourseScreen_Input_MinAttendance"),
                        placeholder = { Text("Min Required Attendance", fontSize = 16.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(0.dp, 8.dp, 0.dp, 8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = LightGray,
                            focusedContainerColor = LightGray
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GreenLight)
                            .testTag("AddCourseScreen_InfoBox")
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("This course will be added to the current semester: ${sem.name}, year ${sem.year}\n")

                                withStyle(
                                    SpanStyle(
                                        color = GreenSecondary,
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = TextDecoration.Underline
                                    )
                                ) {
                                    append("Click here to change semester")
                                }
                            },
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(16.dp)
                                .noRippleClickable {
                                    scope.launch { semSheetState.show() }
                                }
                                .testTag("AddCourseScreen_InfoText"),
                            textAlign = TextAlign.Center
                        )

                        AllSemestersBottomSheet(
                            sheetState = semSheetState,
                            semesterList = state.allSemesters,
                            onDismiss = { scope.launch { semSheetState.hide() } },
                            onSemesterClick = {
                                scope.launch { semSheetState.hide() }
                                vm.updateCurrentSemester(it)
                            },
                            selectedSemesterId = state.activeSemester?.id ?: "",
                            buttonColor = GreenSecondary,
                            onAddSemester = {
                                scope.launch { semSheetState.hide() }
                                onNavigateToAddSemester()
                            }
                        )
                    }

                    Button(
                        onClick = { vm.onSubmit(onNavigateBack) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenSecondary,
                        ),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(8.dp, 0.dp, 8.dp, 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("AddCourseScreen_SubmitButton"),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text("Submit", color = Color.White, fontSize = 18.sp)
                    }
                }

                state.errorMsg?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 16.sp,
                        modifier = Modifier.testTag("AddCourseScreen_ErrorText")
                    )
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("AddCourseScreen_LoadingContainer"),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GreenNormal,
                    modifier = Modifier.testTag("AddCourseScreen_LoadingIndicator")
                )
            }
        }
    }
}