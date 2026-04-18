package com.studypulse.app.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.studypulse.feat.auth.nav.authNavGraph
import com.studypulse.app.LocalCurrentUser
import com.studypulse.feat.attendance.nav.attendanceGraph
import com.studypulse.app.feat.feedback.FeedbackScreen
import com.studypulse.app.feat.home.nav.homeGraph
import com.studypulse.feat.flashcards.nav.flashcardNavGraph
import com.studypulse.feat.semester.nav.semesterNavGraph
import com.studypulse.feat.user.nav.userNavGraph
import com.studypulse.nav.routes.Route

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = if (LocalCurrentUser.current != null) Route.HomeRoute else Route.SignUpRoute,
    ) {
        attendanceGraph(navController)
        homeGraph(navController)
        authNavGraph(navController)
        userNavGraph(navController)
        semesterNavGraph(navController)
        flashcardNavGraph(navController)

        composable<Route.FeedbackRoute> {
            FeedbackScreen()
        }
    }
}
