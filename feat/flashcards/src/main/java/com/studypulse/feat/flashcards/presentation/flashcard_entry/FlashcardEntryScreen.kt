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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.presentation.AddPackBottomSheet
import com.studypulse.nav.routes.FcpListType
import com.studypulse.ui.R
import com.studypulse.ui.components.LargeAppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Blue
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.Gold
import com.studypulse.ui.theme.GreenSecondary
import com.studypulse.ui.theme.Orange
import com.studypulse.ui.theme.Pink
import com.studypulse.ui.theme.Purple
import com.studypulse.ui.theme.Typography
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private val sampleFc = Flashcard(
    id = "dx",
    "What is the time complexity of binary search",
    "O(log n)",
    "wee",
    "sfs",
    "iy"
)

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
    val pagerState = rememberPagerState { 4 }
    var showAddPackSheet by rememberSaveable { mutableStateOf(false) }

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
                        flashcards = listOf(sampleFc, sampleFc, sampleFc, sampleFc),
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
                    FlashcardItem(fc = flashcards[page % flashcards.size])
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
    val tileWidth = 140.dp
    val tileHeight = 150.dp
    val mildPackColor = pack.color.copy(alpha = 0.08f)
    val mildCyan = Cyan.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .width(tileWidth)
            .height(tileHeight)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(listOf(mildCyan, mildPackColor))
            )
            .noRippleClickable { onClick() },
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(pack.color)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = pack.title,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (pack.isPublic) "PUBLIC" else "PRIVATE",
                    style = Typography.labelSmall,
                    color = Cyan,
                )
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
            .width(140.dp)
            .height(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Cyan.copy(alpha = 0.10f))
            .border(
                width = 1.5.dp,
                color = Cyan.copy(alpha = 0.6f),
                shape = RoundedCornerShape(14.dp),
            )
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
                color = DarkGray,
            )
        }
    }
}

@Composable
fun FlashcardItem(
    fc: Flashcard,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
    width: Dp = 220.dp,
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
            }
            .noRippleClickable { flipped = !flipped },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .height(height)
                .fillMaxWidth(0.8f)
                .graphicsLayer { rotationY = if (isShowingAnswer) 180f else 0f },
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Cyan.copy(alpha = 0.4f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isShowingAnswer) fc.answer else fc.question,
                    style = Typography.bodyMedium,
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = "Tap to flip",
                    style = Typography.labelSmall,
                    color = DarkGray.copy(alpha = 0.45f),
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}