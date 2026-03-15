package com.example.feat.auth.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.feat.auth.signin.SignInScreen
import com.example.feat.auth.signup.SignupScreen
import com.studypulse.nav.routes.Route

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    composable<Route.SignUpRoute> {
        SignupScreen(
            navigateToSignIn = {
                navController.navigate(Route.SignInRoute)
            },
        )
    }

    composable<Route.SignInRoute> {
        SignInScreen(
            navigateToSignUp = {
                navController.navigate(Route.SignUpRoute)
            }
        )
    }
}