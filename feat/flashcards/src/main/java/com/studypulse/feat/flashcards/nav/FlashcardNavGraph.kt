package com.studypulse.feat.flashcards.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.studypulse.feat.flashcards.presentation.fcp_details.FlashcardPackDetailsScreen
import com.studypulse.feat.flashcards.presentation.fcp_list.FlashcardPackListScreen
import com.studypulse.feat.flashcards.presentation.flashcard_details.FlashcardDetailsScreen
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardEntryScreen
import com.studypulse.feat.flashcards.presentation.study_session.StudySessionScreen
import com.studypulse.nav.routes.Route

fun NavGraphBuilder.flashcardNavGraph(navController: NavController) {

    composable<Route.FlashcardEntryRoute> {
        FlashcardEntryScreen(
            onNavigateToProfile = { navController.navigate(Route.ProfileRoute) },
            onNavigateToFcpScreen = { navController.navigate(Route.FcpDetailsRoute(it)) },
            onNavigateToPackListScreen = { navController.navigate(Route.FcpListRoute(it)) },
            onNavigateToFcDetails = { id, packId ->
                navController.navigate(Route.FlashcardDetailsRoute(id, packId, false))
            },
            onNavigateToStudySession = {
                navController.navigate(Route.StudySessionRoute(packId = null))
            },
        )
    }

    composable<Route.FcpDetailsRoute> {
        FlashcardPackDetailsScreen(
            navigateToFcDetails = { id, packId, editing -> navController.navigate(Route.FlashcardDetailsRoute(id, packId, editing)) },
            onBack = { navController.navigateUp() },
            onStudy = { packId -> navController.navigate(Route.StudySessionRoute(packId = packId)) },
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

    composable<Route.StudySessionRoute> {
        StudySessionScreen(
            onBack = { navController.navigateUp() },
        )
    }
}
