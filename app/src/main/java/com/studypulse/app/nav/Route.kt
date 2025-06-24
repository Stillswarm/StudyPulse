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
    data class CourseDetailRoute(val courseId: Long)

    @Serializable
    data class AddPeriodRoute(val courseId: Long)

    @Serializable
    data class ScheduleRoute(val courseId: Long?)

    @Serializable
    data object AttendanceCalendarRoute

    @Serializable
    data object AttendanceRoute

    @Serializable
    data object AttendanceOverviewRoute

    @Serializable
    data class AttendanceDetailsRoute(val courseId: Long)

    @Serializable
    data object AttendanceStatsSharedRoute


    // AUTH ROUTES START

    @Serializable
    data object SignUpRoute

    @Serializable
    data object SignInRoute

    // AUTH ROUTES END
}