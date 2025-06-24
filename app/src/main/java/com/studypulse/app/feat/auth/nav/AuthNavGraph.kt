package com.studypulse.app.feat.auth.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.app.feat.auth.signup.SignupScreen
import com.studypulse.app.nav.Route

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    composable<Route.SignUpRoute> {
        SignupScreen(
            navigateToSignIn = {
                navController.navigate(Route.SignInRoute)
            },
            navigateToHome = {
                navController.navigate(Route.HomeRoute)
            }
        )
    }
}