package com.studypulse.feat.flashcards.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.feat.flashcards.presentation.fcp_details.FlashcardPackDetailsScreen
import com.studypulse.feat.flashcards.presentation.fcp_list.FlashcardPackListScreen
import com.studypulse.feat.flashcards.presentation.flashcard_details.FlashcardDetailsScreen
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardEntryScreen
import com.studypulse.nav.routes.Route

fun NavGraphBuilder.flashcardNavGraph(navController: NavController) {

    composable<Route.FlashcardEntryRoute> {
        FlashcardEntryScreen(
            onNavigateToProfile = { navController.navigate(Route.ProfileRoute) },
            onNavigateToFcpScreen = { navController.navigate(Route.FcpDetailsRoute(it)) },
            onNavigateToPackListScreen = { navController.navigate(Route.FcpListRoute(it) )}
        )
    }

    composable<Route.FcpDetailsRoute> {
        FlashcardPackDetailsScreen(
            navigateToFcDetails = { id, packId, editing -> navController.navigate(Route.FlashcardDetailsRoute(id, packId, editing)) }
        )
    }

    composable<Route.FlashcardDetailsRoute> {
        FlashcardDetailsScreen(
            onBack = { navController.navigateUp() },
        )
    }

    composable<Route.FcpListRoute> {
        FlashcardPackListScreen(
            onBack = { navController.navigateUp() },
            onPackClick = { navController.navigate(Route.FcpDetailsRoute(it))},
        )
    }


}