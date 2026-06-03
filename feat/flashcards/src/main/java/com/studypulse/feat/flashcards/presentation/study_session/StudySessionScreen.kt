package com.studypulse.feat.flashcards.presentation.study_session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardItem
import com.studypulse.ui.components.AppTopBar
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.Typography
import com.studypulse.ui.theme.WarmWhite
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel
import com.studypulse.feat.flashcards.R as FcR

@Composable
fun StudySessionScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: StudySessionScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val cards = state.page.cards
    val pagerState = rememberPagerState(pageCount = { cards.size })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> vm.maybePrefetch(page) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmWhite),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                backgroundColor = Cyan,
                foregroundGradient = null,
                title = "Study session",
                titleColor = Color.White,
                navigationIcon = FcR.drawable.ic_arrow_left,
                onNavigationClick = onBack,
                actionIcon = null,
                onActionClick = null,
            )

            when {
                state.loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Cyan)
                }

                cards.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No cards to study right now.",
                        style = Typography.bodyMedium,
                        color = DarkGray.copy(alpha = 0.7f),
                    )
                }

                else -> StudyPager(
                    pagerState = pagerState,
                    cards = cards,
                    onFeedback = vm::onCardFeedback,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun StudyPager(
    pagerState: PagerState,
    cards: List<Sm2Flashcard>,
    onFeedback: (Sm2Flashcard, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Card ${pagerState.currentPage + 1} of ${cards.size}",
                style = Typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray.copy(alpha = 0.75f),
            )
        }

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 12.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val card = cards[page]
            StudyCardPage(
                fc = card.flashcard,
                onFeedback = { score -> onFeedback(card, score) },
            )
        }

        PageIndicator(
            count = cards.size,
            current = pagerState.currentPage,
        )
    }
}

@Composable
private fun StudyCardPage(
    fc: Flashcard,
    onFeedback: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAnswerSide by remember(fc.id) { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            FlashcardItem(
                fc = fc,
                onFeedback = { fb -> onFeedback(fb.score) },
                onSideChanged = { isAnswer -> showAnswerSide = isAnswer },
                height = 320.dp,
            )
        }

        if (showAnswerSide && !fc.description.isNullOrBlank()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Cyan.copy(alpha = 0.08f))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "DESCRIPTION",
                        style = Typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray.copy(alpha = 0.65f),
                    )
                    Text(
                        text = fc.description,
                        style = Typography.bodyMedium,
                        color = DarkGray.copy(alpha = 0.85f),
                    )
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun PageIndicator(
    count: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(count) { i ->
            val active = current == i
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(if (active) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (active) Cyan else Cyan.copy(alpha = 0.3f))
            )
        }
    }
}
