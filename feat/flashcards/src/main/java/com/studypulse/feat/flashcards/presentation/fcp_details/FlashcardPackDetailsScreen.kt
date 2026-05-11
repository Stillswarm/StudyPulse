package com.studypulse.feat.flashcards.presentation.fcp_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun FlashcardPackDetailsScreen(
    modifier: Modifier = Modifier,
    navigateToFcDetails: (id: String?, editing: Boolean) -> Unit,
    vm: FlashcardPackDetailsScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
}