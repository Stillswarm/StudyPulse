package com.studypulse.app.nav

import com.studypulse.app.feat.attendance.courses.domain.model.Day
import kotlinx.serialization.Serializable

interface NavigableRoute

object Route {
    @Serializable
    data object HomeRoute : NavigableRoute

    @Serializable
    data object CourseRoute : NavigableRoute

    @Serializable
    data object AddCourseRoute : NavigableRoute

    @Serializable
    data class CourseDetailRoute(val courseId: String) : NavigableRoute

    @Serializable
    data class AddPeriodRoute(val courseId: String, val day: Day? = null) : NavigableRoute

    @Serializable
    data class ScheduleRoute(val courseId: String?) : NavigableRoute

    @Serializable
    data object AttendanceCalendarRoute : NavigableRoute

    @Serializable
    data object AttendanceRoute : NavigableRoute

    @Serializable
    data object AttendanceOverviewRoute : NavigableRoute

    @Serializable
    data class AttendanceDetailsRoute(val courseId: String) : NavigableRoute

    @Serializable
    data object AttendanceStatsSharedRoute : NavigableRoute

    // AUTH ROUTES START

    @Serializable
    data object SignUpRoute : NavigableRoute

    @Serializable
    data object SignInRoute : NavigableRoute

    @Serializable
    data object ProfileRoute : NavigableRoute

    // AUTH ROUTES END

    @Serializable
    data object AddSemesterRoute : NavigableRoute

}