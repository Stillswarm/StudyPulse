package com.studypulse.app.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.studypulse.app.feat.attendance.nav.attendanceGraph
import com.studypulse.app.feat.home.nav.homeGraph

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.HomeRoute,
    ) {
        attendanceGraph(navController)
        homeGraph(navController)
    }
}