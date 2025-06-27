package com.studypulse.app.feat.semester

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.app.feat.semester.presentation.AddSemesterScreen
import com.studypulse.app.nav.Route

fun NavGraphBuilder.semesterNavGraph(navController: NavController) {
    composable<Route.AddSemesterRoute> {
        AddSemesterScreen(
            onNavigateBack = { navController.navigateUp() }
        )
    }
}