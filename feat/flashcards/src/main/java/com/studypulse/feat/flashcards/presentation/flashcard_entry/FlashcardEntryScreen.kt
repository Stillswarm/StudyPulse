package com.studypulse.feat.flashcards.presentation.flashcard_entry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.studypulse.common.event.NavigationDrawerController
import com.studypulse.ui.R
import com.studypulse.ui.components.LargeAppTopBar
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.Purple
import kotlinx.coroutines.launch

@Composable
fun FlashcardEntryScreen(
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        LargeAppTopBar(
            backgroundColor = Cyan,
            title = "Never miss a Concept!",
            navigationIcon = R.drawable.logo_pulse,
            onNavigationClick = { scope.launch { NavigationDrawerController.toggle() } },
            actionIcon = R.drawable.ic_profile,
            onActionClick = onNavigateToProfile,
            foregroundGradient = Brush.linearGradient(
                colorStops = arrayOf(
                    Pair(0f, Purple),
                    Pair(59f, Color.Black)
                )
            ),
//            imageRes = R.drawable.im_books_black,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
    }
}