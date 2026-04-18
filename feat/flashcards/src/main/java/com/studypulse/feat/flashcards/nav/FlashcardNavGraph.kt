package com.studypulse.feat.flashcards.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardEntryScreen
import com.studypulse.nav.routes.Route

fun NavGraphBuilder.flashcardNavGraph(navController: NavController) {

    composable<Route.FlashcardEntryRoute> {
        FlashcardEntryScreen(
            onNavigateToProfile = { navController.navigate(Route.ProfileRoute) }
        )
    }

}