package com.studypulse.app.feat.user.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.app.feat.user.presentation.ProfileScreen
import com.studypulse.app.nav.Route

fun NavGraphBuilder.userNavGraph(navController: NavController) {
    composable<Route.ProfileRoute> {
        ProfileScreen(
            navigateToLogin = { navController.navigate(Route.ProfileRoute) },
            onAddNewSemester = { navController.navigate(Route.AddSemesterRoute) }
        )
    }
}