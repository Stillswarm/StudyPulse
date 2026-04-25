package com.studypulse.feat.flashcards.presentation.flashcard_entry

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.common.event.NavigationDrawerController
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.ui.R
import com.studypulse.ui.components.LargeAppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.Purple
import com.studypulse.ui.theme.StudyPulseTheme
import com.studypulse.ui.theme.Typography
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

val sampleFc = Flashcard(
    id = "dx",
    "What is the time complexity of binary search",
    "O(n logn)",
    "wee",
    "sfs",
    "iy"
)

val sampleFcPack = FlashcardPack(
    id = "sfs",
    ownerId = "iy",
    title = "Computer Science",
    description = "",
    color = 0xFFFFFF
)

@Composable
fun FlashcardEntryScreen(
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    vm: FlashcardEntryScreenViewModel = koinViewModel(),
) {

    val state by vm.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 4 }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        LargeAppTopBar(
            backgroundColor = Cyan,
            title = "Revisions made easy!",
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
            imageRes = R.drawable.im_flashcards_black,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Spacer(Modifier.height(250.dp)) }
            item { QuickRevisionCarousel(pagerState = pagerState) }
            item { HorizontalCardListWithTitle(
                title = "Your Packs",
                items = listOf(sampleFcPack, sampleFcPack, sampleFcPack)
            ) }
            item { HorizontalCardListWithTitle(
                title = "Your Packs",
                items = listOf(sampleFcPack, sampleFcPack, sampleFcPack)
            ) }
        }
    }
}

@Composable
fun QuickRevisionCarousel(modifier: Modifier = Modifier, pagerState: PagerState) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = DarkGray, shape = RoundedCornerShape(16.dp))
    ) {
        Column {
            Text(
                text = "Quick Revision",
                style = Typography.displayMedium,
                modifier = Modifier.padding(16.dp)
            )

            HorizontalPager(
                modifier = modifier,
                state = pagerState,
                contentPadding = PaddingValues(16.dp)
            ) {
                FlashcardItem(modifier = Modifier.padding(end = 16.dp), fc = sampleFc)
            }
        }
    }
}

@Composable
fun HorizontalCardListWithTitle(
    title: String,
    items: List<FlashcardPack>,
    modifier: Modifier = Modifier,
) {
    Column {
        Text(
            text = title,
        )

        LazyRow {
            items(
                items = items,
//                key = { it.id },
            ) {
                Image(
                    painter = painterResource(R.drawable.im_fc_pack),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            0.5.dp, DarkGray,
                            RoundedCornerShape(8.dp)
                        ),
                )
            }
        }

    }
}

@Composable
fun FlashcardItem(
    fc: Flashcard,
    modifier: Modifier = Modifier,
) {

    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "card flip"
    )

    val isShowingAnswer = rotation > 90

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .noRippleClickable { flipped = !flipped },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { rotationY = if (isShowingAnswer) 180f else 0f }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isShowingAnswer) fc.answer else fc.question,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Text(
                    text = "Tap to flip",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}