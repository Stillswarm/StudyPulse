package com.studypulse.app.nav

import kotlinx.serialization.Serializable

object Route {
    @Serializable
    data object HomeRoute

    @Serializable
    data object CourseRoute

    @Serializable
    data object AddCourseRoute

    @Serializable
    data class CourseDetailRoute(val courseId: String)

    @Serializable
    data class AddPeriodRoute(val courseId: String)

    @Serializable
    data class ScheduleRoute(val courseId: String?)

    @Serializable
    data object AttendanceCalendarRoute

    @Serializable
    data object AttendanceRoute

    @Serializable
    data object AttendanceOverviewRoute

    @Serializable
    data class AttendanceDetailsRoute(val courseId: String)

    @Serializable
    data object AttendanceStatsSharedRoute


    // AUTH ROUTES START

    @Serializable
    data object SignUpRoute

    @Serializable
    data object SignInRoute

    @Serializable
    data object ProfileRoute

    // AUTH ROUTES END

    @Serializable
    data object AddSemesterRoute

}