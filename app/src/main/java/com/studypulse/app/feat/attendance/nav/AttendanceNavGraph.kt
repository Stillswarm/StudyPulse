package com.studypulse.app.feat.attendance.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.app.feat.attendance.attendance.ui.AttendanceScreen
import com.studypulse.app.feat.attendance.calender.ui.AttendanceCalendarScreen
import com.studypulse.app.feat.attendance.courses.presentation.add_course.AddCourseScreen
import com.studypulse.app.feat.attendance.courses.presentation.add_period.AddPeriodScreen
import com.studypulse.app.feat.attendance.courses.presentation.course.CoursesScreen
import com.studypulse.app.feat.attendance.courses.presentation.course_details.CourseDetailsScreen
import com.studypulse.app.feat.attendance.schedule.presentation.ScheduleScreen
import com.studypulse.app.nav.Route

fun NavGraphBuilder.attendanceGraph(navController: NavController) {
    composable<Route.CourseRoute> {
        CoursesScreen(
            onAddNewCourse = {
                navController.navigate(Route.AddCourseRoute)
            },
            onCourseDetails = {
                navController.navigate(Route.CourseDetailRoute(it))
            }
        )
    }

    composable<Route.AddCourseRoute> {
        AddCourseScreen(
            onNavigateBack = { navController.navigateUp() }
        )
    }

    composable<Route.CourseDetailRoute> {
        CourseDetailsScreen(
            onNavigateBack = { navController.navigateUp() },
            onNavigateToSchedule = {
                navController.navigate(
                    Route.ScheduleRoute(it)
                )
            }
        )
    }

    composable<Route.ScheduleRoute> {
        ScheduleScreen(
            onNavigateToFullSchedule = { navController.navigate(Route.ScheduleRoute(null)) },
            navigateToAddPeriod = { navController.navigate(Route.AddPeriodRoute(it)) }
        )
    }

    composable<Route.AddPeriodRoute> {
        AddPeriodScreen(
            onNavigateBack = { navController.navigateUp() }
        )
    }

    composable<Route.AttendanceCalendarRoute> {
        AttendanceCalendarScreen()
    }

    composable<Route.AttendanceRoute> {
        AttendanceScreen(
            onNavigateBack = { navController.navigateUp() },
            onNavigateToCourseList = { navController.navigate(Route.CourseRoute) },
            onNavigateToAttendanceCalendar = { navController.navigate(Route.AttendanceCalendarRoute) }
        )
    }
}