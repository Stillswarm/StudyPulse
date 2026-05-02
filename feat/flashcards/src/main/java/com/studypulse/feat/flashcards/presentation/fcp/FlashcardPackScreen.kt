package com.studypulse.feat.flashcards.presentation.fcp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun FlashcardPackScreen(
    modifier: Modifier = Modifier,
    vm: FlashcardPackScreenViewModel = koinViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
}