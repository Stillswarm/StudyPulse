package com.studypulse.feat.flashcards.presentation.flashcard_details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun FlashcardDetailsScreen(
    modifier: Modifier = Modifier,
    vm: FlashcardDetailsScreenViewModel = koinViewModel(),
) {

    val state by vm.state.collectAsStateWithLifecycle()

}