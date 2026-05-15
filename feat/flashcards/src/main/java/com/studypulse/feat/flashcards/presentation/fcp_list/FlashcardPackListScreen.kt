package com.studypulse.feat.flashcards.presentation.fcp_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import org.koin.androidx.compose.koinViewModel

@Composable
fun FlashcardPackListScreen(
    modifier: Modifier = Modifier,
    vm: FlashcardPackListScreenViewModel = koinViewModel(),
) {

    val list by vm.listStateFlow.collectAsStateWithLifecycle()


}

@Composable
fun PackView(fcp: FlashcardPack, modifier: Modifier = Modifier) {

}

@Preview
@Composable
fun PackViewPreview(modifier: Modifier = Modifier) {
    PackView(
        fcp = FlashcardPack(
            id = "fg5tyru45",
            ownerId = "fg4567",
            title = "Programming in Java",
            description = "Common Java Programming constructs and syntax",
            color = 0x45678,
            isPublic = true,
            createdAt = 1747234800,
            updatedAt = 1747234800,
        )
    )
}