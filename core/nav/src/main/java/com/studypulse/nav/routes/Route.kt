package com.studypulse.nav.routes

import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

interface NavigableRoute

object Route {
    @Serializable
    data object HomeRoute : NavigableRoute

    @Serializable
    data object CourseRoute : NavigableRoute

    @Serializable
    data class AddCourseRoute(val courseId: String? = null) : NavigableRoute

    @Serializable
    data class CourseDetailRoute(val courseId: String) : NavigableRoute

    /** day: DayOfWeek.name for serialization; use DayOfWeek.valueOf(day) at destination */
    @Serializable
    data class AddPeriodRoute(val courseId: String, val periodId: String? = null, val day: String? = null) : NavigableRoute

    @Serializable
    data class ScheduleRoute(val courseId: String?, val courseCode: String?) : NavigableRoute

    @Serializable
    data object AttendanceCalendarRoute : NavigableRoute

    @Serializable
    data object AttendanceRoute : NavigableRoute

    @Serializable
    data class AttendanceOverviewRoute(val overviewType: String = OverviewType.ALL.name) : NavigableRoute

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

    @Serializable
    data object FeedbackRoute : NavigableRoute

    // FLASHCARD ROUTES
    @Serializable
    data object FlashcardEntryRoute : NavigableRoute

}

fun NavBackStackEntry.isRoute(routeClass: KClass<out NavigableRoute>): Boolean {
    return this.destination.route?.contains(routeClass.simpleName ?: "") == true
}

enum class OverviewType {
    ALL,
    LOW,
    FULL
}