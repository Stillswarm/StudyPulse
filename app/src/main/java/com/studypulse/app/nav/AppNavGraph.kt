package com.studypulse.app.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.studypulse.app.LocalCurrentUser
import com.studypulse.app.feat.attendance.nav.attendanceGraph
import com.studypulse.app.feat.auth.nav.authNavGraph
import com.studypulse.app.feat.home.nav.homeGraph

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
    }
}