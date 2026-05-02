package com.studypulse.feat.flashcards.presentation.flashcard_entry

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.common.event.NavigationDrawerController
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.ui.R
import com.studypulse.ui.components.LargeAppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.Purple
import com.studypulse.ui.theme.SeafoamWhite
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
    title = "Computer Science Physics Chemistry Maths",
    description = "",
    color = 0xFF40E0D0.toInt()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardEntryScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToFcpScreen: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    vm: FlashcardEntryScreenViewModel = koinViewModel(),
) {

    val state by vm.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 4 }
    var showAddPackSheet by remember { mutableStateOf(false) }

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

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(200.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { QuickRevisionCarousel(pagerState = pagerState) }
                item {
                    HorizontalCardListWithTitle(
                        title = "Your Packs",
                        items = listOf(sampleFcPack, sampleFcPack, sampleFcPack),
                        addNew = true,
                        onAddNewClick = { showAddPackSheet = true }
                    )
                }
                item {
                    HorizontalCardListWithTitle(
                        title = "Popular Packs",
                        items = listOf(sampleFcPack, sampleFcPack, sampleFcPack),
                        addNew = false
                    )
                }
            }
        }
    }

    if (showAddPackSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAddPackSheet = false },
            sheetState = sheetState,
            dragHandle = {},
            shape = RectangleShape,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = state.newFcpTitle,
                    onValueChange = vm::onNewFcpTitleChange,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    )
                )

                Button(
                    onClick = {
                        showAddPackSheet = false
                        vm.addAndNavigate(onNavigateToFcpScreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                ) {
                    Text(
                        text = "Create",
                        fontSize = 16.sp,
                        color = Color.White,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickRevisionCarousel(modifier: Modifier = Modifier, pagerState: PagerState) {

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = SeafoamWhite),
        border = BorderStroke(2.dp, Cyan)
    ) {
        Column {
            Text(
                text = "Quick Revision",
                style = Typography.headlineSmall,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 12.dp)
            )

            HorizontalPager(
                modifier = modifier,
                state = pagerState,
                contentPadding = PaddingValues(12.dp),
                key = { it }
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
    addNew: Boolean = true,
    onAddNewClick: (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
        )

        LazyRow {
            if (addNew) {
                item {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                Dp.Hairline, Cyan,
                                RoundedCornerShape(8.dp)
                            )
                            .noRippleClickable { onAddNewClick?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "add new",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            items(
                items = items,
//                key = { it.id },
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.im_fc_pack),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                0.5.dp, Cyan,
                                RoundedCornerShape(8.dp)
                            ),
//                        colorFilter = ColorFilter.tint(Color(it.color).copy(alpha = 0.1f), blendMode = BlendMode.Modulate)
                    )

                    Text(
                        text = it.title,
                        style = Typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(top = 4.dp),
                    )
                }
            }
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