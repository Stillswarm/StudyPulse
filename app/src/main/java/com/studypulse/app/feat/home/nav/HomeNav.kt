package com.studypulse.app.feat.home.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.app.feat.home.ui.HomeScreen
import com.studypulse.app.nav.Route

fun NavGraphBuilder.homeGraph(navController: NavController) {
    composable<Route.HomeRoute> {
        HomeScreen(
            navigate = { navController.navigate(it) },
        )
    }
}