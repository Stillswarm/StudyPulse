package com.studypulse.feat.flashcards.presentation.fcp_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun FlashcardPackListScreen(
    modifier: Modifier = Modifier,
    vm: FlashcardPackListScreenViewModel = koinViewModel(),
) {

    val list by vm.listStateFlow.collectAsStateWithLifecycle()


}