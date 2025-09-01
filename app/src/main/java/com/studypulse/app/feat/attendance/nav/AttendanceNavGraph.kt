package com.studypulse.app.feat.attendance.nav

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.attendance.attendance.presentation.AttendanceStatsSharedViewModel
import com.studypulse.app.feat.attendance.attendance.presentation.details.AttendanceDetailsScreen
import com.studypulse.app.feat.attendance.attendance.presentation.home.AttendanceScreen
import com.studypulse.app.feat.attendance.attendance.presentation.overview.AttendanceOverviewScreen
import com.studypulse.app.feat.attendance.calender.ui.AttendanceCalendarScreen
import com.studypulse.app.feat.attendance.courses.presentation.add_course.AddCourseScreen
import com.studypulse.app.feat.attendance.courses.presentation.add_period.AddPeriodScreen
import com.studypulse.app.feat.attendance.courses.presentation.course.CoursesScreen
import com.studypulse.app.feat.attendance.courses.presentation.course_details.CourseDetailsScreen
import com.studypulse.app.feat.attendance.schedule.presentation.ScheduleScreen
import com.studypulse.app.nav.Route
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.attendanceGraph(navController: NavController) {
    composable<Route.CourseRoute> {
        CoursesScreen(
            navigateBack = { navController.navigateUp() },
            onAddNewCourse = {
                navController.navigate(Route.AddCourseRoute(it))
            },
            onCourseTimetable = { cId, cCode ->
                navController.navigate(Route.ScheduleRoute(cId, cCode))
            }
        )
    }

    composable<Route.AddCourseRoute> {
        AddCourseScreen(
            onNavigateBack = { navController.navigateUp() },
            onNavigateToAddSemester = { navController.navigate(Route.AddSemesterRoute) }
        )
    }

    composable<Route.CourseDetailRoute> {
        CourseDetailsScreen(
            onNavigateBack = { navController.navigateUp() },
            onNavigateToSchedule = { cId, cCode ->
                navController.navigate(
                    Route.ScheduleRoute(cId, cCode)
                )
            }
        )
    }

    composable<Route.ScheduleRoute> {
        ScheduleScreen(
            onNavigateToFullSchedule = { navController.navigate(Route.ScheduleRoute(null, null)) },
            navigateToAddPeriod = { courseId, periodId, day ->
                navController.navigate(Route.AddPeriodRoute(courseId, periodId, day)) },
            onNavigateBack = { navController.navigateUp() }
        )
    }

    composable<Route.AddPeriodRoute> {
        AddPeriodScreen(
            onNavigateBack = { navController.navigateUp() }
        )
    }

    composable<Route.AttendanceCalendarRoute> {
        AttendanceCalendarScreen(
            navigateUp = { navController.navigateUp() }
        )
    }

    composable<Route.AttendanceRoute> {
        AttendanceScreen(
            onNavigateToProfile = { navController.navigate(Route.ProfileRoute) },
            onNavigateToCourseList = { navController.navigate(Route.CourseRoute) },
            onNavigateToAttendanceCalendar = { navController.navigate(Route.AttendanceCalendarRoute) },
            onNavigateToAttendanceOverview = { navController.navigate(Route.AttendanceOverviewRoute(it)) },
            onNavigateToAddSemester = { navController.navigate(Route.AddSemesterRoute) }
        )
    }

//    navigation<Route.AttendanceStatsSharedRoute>(
//        startDestination = Route.AttendanceOverviewRoute()
//    ) {
        composable<Route.AttendanceOverviewRoute> { backStackEntry ->

//            val parentEntry = remember(backStackEntry) {
//                navController.getBackStackEntry<Route.AttendanceStatsSharedRoute>()
//            }
//            val sharedViewModel: AttendanceStatsSharedViewModel =
//                koinViewModel(viewModelStoreOwner = parentEntry)
//            val attendanceByCourse by sharedViewModel.attendanceByCourse.collectAsStateWithLifecycle()
            AttendanceOverviewScreen(
                onNavigateToProfile = { navController.navigate(Route.ProfileRoute) },
                onDetails = { navController.navigate(Route.AttendanceDetailsRoute(it)) },
                )
        }

        composable<Route.AttendanceDetailsRoute> { backStackEntry ->

            val scope = rememberCoroutineScope()
            val route = backStackEntry.toRoute<Route.AttendanceDetailsRoute>()
            val courseId = route.courseId
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Route.AttendanceStatsSharedRoute>()
            }
            val sharedViewModel: AttendanceStatsSharedViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)
            val attendanceByCourse by sharedViewModel.attendanceByCourse.collectAsStateWithLifecycle()
            val allCoursesMap by sharedViewModel.allCoursesMap.collectAsStateWithLifecycle()

            if (allCoursesMap[courseId] == null) {
                scope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = "cannot fetch details")
                    )
                    navController.navigateUp()
                }
            } else {
                AttendanceDetailsScreen(
                    course = allCoursesMap[courseId]!!,
                    attendanceRecords = attendanceByCourse[courseId] ?: emptyList(),
                    onBack = { navController.navigateUp() },
                    viewModel = sharedViewModel
                )
            }
        }
//    }
}
