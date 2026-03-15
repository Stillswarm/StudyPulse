package com.studypulse.feat.semester.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.feat.semester.presentation.AddSemesterScreen
import com.studypulse.nav.routes.Route

fun NavGraphBuilder.semesterNavGraph(navController: NavController) {
    composable<Route.AddSemesterRoute> {
        AddSemesterScreen(
            onNavigateBack = { navController.navigateUp() }
        )
    }
}
