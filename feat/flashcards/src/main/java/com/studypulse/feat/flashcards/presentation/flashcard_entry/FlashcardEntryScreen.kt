package com.studypulse.feat.flashcards.presentation.flashcard_entry

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.common.event.NavigationDrawerController
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardFeedback
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.presentation.AddPackBottomSheet
import com.studypulse.nav.routes.FcpListType
import com.studypulse.ui.R
import com.studypulse.ui.components.LargeAppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.GreenDark
import com.studypulse.ui.theme.GreenSecondary
import com.studypulse.ui.theme.Orange
import com.studypulse.ui.theme.Purple
import com.studypulse.ui.theme.Red
import com.studypulse.ui.theme.Typography
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

const val PREFETCH_THRESHOLD = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardEntryScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToFcpScreen: (id: String) -> Unit,
    onNavigateToPackListScreen: (FcpListType) -> Unit,
    modifier: Modifier = Modifier,
    vm: FlashcardEntryScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val flashcards = state.quickRevisionPage.cards
    val pagerState = rememberPagerState { flashcards.size }
    var showAddPackSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow {
            pagerState.currentPage
        }.distinctUntilChanged()
            .collect { page ->
                if (page >= state.quickRevisionPage.cards.size - PREFETCH_THRESHOLD) {
                    vm.getRandomCards()
                }
            }
    }

    Box(modifier = modifier.fillMaxSize()) {
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
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(184.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                item {
                    QuickRevisionCarousel(
                        flashcards = state.quickRevisionPage.cards,
                        pagerState = pagerState,
                    )
                }

                item {
                    HorizontalCardListWithTitle(
                        title = "Your Packs",
                        items = state.userPacks,
                        addNew = true,
                        onAddNewClick = { showAddPackSheet = true },
                        onSeeAllClick = { onNavigateToPackListScreen(FcpListType.USER) },
                        onPackClick = { onNavigateToFcpScreen(it.id) },
                    )
                }

                item {
                    HorizontalCardListWithTitle(
                        title = "Popular Packs",
                        items = state.popularPacks,
                        addNew = false,
                        onSeeAllClick = { onNavigateToPackListScreen(FcpListType.POPULAR) },
                        onPackClick = { onNavigateToFcpScreen(it.id) },
                    )
                }

                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    if (showAddPackSheet) {
        AddPackBottomSheet(
            fcp = state.newFcp,
            onTitleChange = vm::onNewFcpTitleChange,
            onDescriptionChange = vm::onNewFcpDescriptionChange,
            onColorPick = vm::onNewFcpColorChange,
            onPublicToggle = vm::onNewFcpVisibilityToggle,
            onCreate = {
                vm.addNewPackAndNavigate(onNavigateToFcpScreen)
            },
            onDismiss = { showAddPackSheet = false },
        )
    }
}

@Composable
fun QuickRevisionCarousel(
    flashcards: List<Flashcard>,
    pagerState: PagerState,
    onFeedback: (Flashcard, FlashcardFeedback) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, Cyan),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Quick Revision",
                style = Typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            if (flashcards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Add a pack to start revising",
                        style = Typography.bodyMedium,
                        color = DarkGray.copy(alpha = 0.6f),
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) { page ->
                    val card = flashcards[page % flashcards.size]
                    FlashcardItem(
                        fc = card,
                        onFeedback = { fb -> onFeedback(card, fb) },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(flashcards.size) { i ->
                        val active = pagerState.currentPage == i
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
        }
    }
}

@Composable
fun HorizontalCardListWithTitle(
    title: String,
    items: List<FlashcardPack>,
    modifier: Modifier = Modifier,
    addNew: Boolean = false,
    onAddNewClick: (() -> Unit)? = null,
    onSeeAllClick: (() -> Unit)? = null,
    onPackClick: (FlashcardPack) -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = Typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
            )
            if (onSeeAllClick != null) {
                Text(
                    text = "See all  ›",
                    style = Typography.labelLarge,
                    color = Cyan,
                    modifier = Modifier.noRippleClickable { onSeeAllClick.invoke() },
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (addNew) {
                item {
                    AddPackTile(onClick = { onAddNewClick?.invoke() })
                }
            }
            items(items) { pack ->
                MiniPackTile(pack = pack, onClick = { onPackClick(pack) })
            }
        }
    }
}

@Composable
private fun MiniPackTile(
    pack: FlashcardPack,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(MINI_PACK_TILE_WIDTH)
            .height(MINI_PACK_TILE_HEIGHT)
            .noRippleClickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(pack.color),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = pack.title,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    pack.description?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = Typography.bodySmall,
                            color = DarkGray.copy(alpha = 0.6f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Cyan.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = if (pack.isPublic) "PUBLIC" else "PRIVATE",
                        style = Typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Cyan,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddPackTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(MINI_PACK_TILE_WIDTH)
            .height(MINI_PACK_TILE_HEIGHT)
            .clip(RoundedCornerShape(16.dp))
            .background(Cyan.copy(alpha = 0.10f))
            .border(
                width = 1.5.dp,
                color = Cyan.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp),
            )
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Cyan),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add pack",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = "New pack",
                style = Typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
            )
        }
    }
}

private val MINI_PACK_TILE_WIDTH = 160.dp
private val MINI_PACK_TILE_HEIGHT = 180.dp

@Composable
fun FlashcardItem(
    fc: Flashcard,
    onFeedback: (FlashcardFeedback) -> Unit = {},
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
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
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .height(height)
                .fillMaxWidth(0.88f)
                .graphicsLayer { rotationY = if (isShowingAnswer) 180f else 0f },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Cyan.copy(alpha = 0.4f)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .noRippleClickable { flipped = !flipped },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isShowingAnswer) fc.answer else fc.question,
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = DarkGray,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (isShowingAnswer) {
                    FeedbackButtonRow(onFeedback = onFeedback)
                } else {
                    Text(
                        text = "Tap to flip",
                        style = Typography.labelSmall,
                        color = DarkGray.copy(alpha = 0.45f),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .noRippleClickable { flipped = !flipped },
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackButtonRow(
    onFeedback: (FlashcardFeedback) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        FeedbackButton(
            label = "AGAIN",
            color = DarkGray,
            onClick = { onFeedback(FlashcardFeedback.BLACKOUT) },
            modifier = Modifier.weight(1f),
        )
        FeedbackButton(
            label = "WRONG",
            color = Red,
            onClick = { onFeedback(FlashcardFeedback.WRONG) },
            modifier = Modifier.weight(1f),
        )
        FeedbackButton(
            label = "HARD",
            color = Orange,
            onClick = { onFeedback(FlashcardFeedback.HARD) },
            modifier = Modifier.weight(1f),
        )
        FeedbackButton(
            label = "GOOD",
            color = GreenSecondary,
            onClick = { onFeedback(FlashcardFeedback.OKAY) },
            modifier = Modifier.weight(1f),
        )
        FeedbackButton(
            label = "EASY",
            color = GreenDark,
            onClick = { onFeedback(FlashcardFeedback.EASY) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FeedbackButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.55f),
                shape = RoundedCornerShape(8.dp),
            )
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = Typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}