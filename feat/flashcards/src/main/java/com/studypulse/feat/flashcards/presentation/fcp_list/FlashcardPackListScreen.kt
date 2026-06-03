package com.studypulse.feat.flashcards.presentation.fcp_list

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.common.R
import com.studypulse.common.utils.DateUtils.toFullString
import com.studypulse.common.utils.DateUtils.toLocalDate
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.nav.routes.FcpListType
import com.studypulse.ui.components.AppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.modifier.shimmer
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel
import com.studypulse.feat.flashcards.R as FcR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardPackListScreen(
    onBack: () -> Unit,
    onPackClick: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    vm: FlashcardPackListScreenViewModel = koinViewModel(),
) {
    val list by vm.list.collectAsStateWithLifecycle()
    val type = vm.type
    val loading by vm.isLoading.collectAsStateWithLifecycle()

    val lazyColumnState = rememberLazyListState()

    // Trigger a fetch when the user scrolls within 5 items of the tail.
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible =
                lazyColumnState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            list.isNotEmpty() && lastVisible >= list.size - 5
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) vm.getNextSet()
    }

    val title = when (type) {
        FcpListType.USER -> "Your Packs"
        FcpListType.POPULAR -> "Popular Packs"
        null -> "Packs"
    }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            loading && list.isEmpty() -> {
                PackListShimmer(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            list.isEmpty() -> {
                EmptyPackListState(
                    isUserList = type == FcpListType.USER,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyColumnState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 116.dp,
                        bottom = 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items = list, key = { it.id }) { pack ->
                        PackView(
                            fcp = pack,
                            onClick = { onPackClick(pack.id) },
                        )
                    }

                    if (loading) {
                        item(key = "loading-shimmer") {
                            PackShimmerRow(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }

        AppTopBar(
            backgroundColor = Cyan,
            foregroundGradient = null,
            title = title,
            titleColor = Color.White,
            navigationIcon = FcR.drawable.ic_arrow_left,
            onNavigationClick = onBack,
            actionIcon = null,
            onActionClick = null,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun PackListShimmer(
    modifier: Modifier = Modifier,
    rowCount: Int = 6,
) {
    Column(
        modifier = modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 116.dp,
                bottom = 24.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(rowCount) {
            PackShimmerRow(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PackShimmerRow(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE6E8EB))
            .shimmer(),
    )
}

@Composable
private fun EmptyPackListState(
    isUserList: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Cyan.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_filled_star),
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.size(36.dp),
            )
        }

        Text(
            text = if (isUserList) "No packs yet" else "Nothing here yet",
            style = Typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = DarkGray,
        )

        Text(
            text = if (isUserList) {
                "Tap + to create your first flashcard pack"
            } else {
                "Popular packs from the community will appear here"
            },
            style = Typography.bodyMedium,
            color = DarkGray.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}
@Composable
fun PackView(
    fcp: FlashcardPack,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val packColor = fcp.color

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Cyan.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (onClick != null) Modifier.noRippleClickable { onClick() } else Modifier
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Coloured sidebar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(color = packColor)
            )

            // Card content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: title + description + badge | star
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Left: title, description, visibility badge
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = fcp.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 0.04.em,
                            color = Color(0xFF0E2A2F),
                        )
                        fcp.description?.let {
                            Text(
                                text = it.lowercase(),
                                maxLines = 2,
                                minLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = Color(0xFF334455).copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                            )
                        }
                        VisibilityBadge(isPublic = fcp.public)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right: star count
                    StarButton(isStarred = fcp.isStarredByUser, count = fcp.starCount)
                }

                // Bottom row: updated date | card count chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "updated ${fcp.updatedAt.toLocalDate().toFullString()}",
                        color = Color(0xFF334455).copy(alpha = 0.55f),
                        fontSize = 10.sp,
                    )
                    CardCountChip(count = fcp.fcCount, packColor = packColor)
                }
            }
        }
    }
}

@Composable
private fun VisibilityBadge(isPublic: Boolean) {
    val label = if (isPublic) "public" else "private"
    val bgColor = if (isPublic) Cyan.copy(alpha = 0.12f) else Color(0xFF64648C).copy(alpha = 0.09f)
    val borderColor = if (isPublic) Cyan.copy(alpha = 0.3f) else Color(0xFF64648C).copy(alpha = 0.2f)
    val textColor = if (isPublic) Color(0xFF0E7490) else Color(0xFF4A4A6A)
    val icon = if (isPublic) Icons.Outlined.Public else Icons.Outlined.Lock

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(0.5.dp, borderColor, CircleShape)
            .background(bgColor)
            .padding(horizontal = 9.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(9.dp)
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.08.em,
            color = textColor,
        )
    }
}

@Composable
private fun StarButton(isStarred: Boolean, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(0.5.dp, Cyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                .background(Cyan.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = "Stars",
                tint = Color(0xFF0891B2),
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = count.toString(),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0E7490),
        )
    }
}

@Composable
private fun CardCountChip(count: Int, packColor: Color) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(packColor.copy(alpha = 0.10f))
            .padding(horizontal = 9.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Style,
            contentDescription = null,
            tint = packColor,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = "$count cards",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = packColor,
        )
    }
}

@Preview
@Composable
fun PackViewPreview(modifier: Modifier = Modifier) {
    Column {
        PackView(
            modifier = modifier,
            fcp = FlashcardPack(
                id = "fg5tyru45",
                ownerId = "fg4567",
                title = "Biology",
                description = "For college exams",
                color = Color.Black,
                public = true,
                createdAt = 1747234800,
                updatedAt = 1747234800,
            )
        )

        Spacer(Modifier.height(16.dp))

        PackView(
            modifier = modifier,
            fcp = FlashcardPack(
                id = "fg5tyru45",
                ownerId = "fg4567",
                title = "Programming in Java",
                description = "Common Java Programming constructs and syntax",
                color = Color.Red,
                public = true,
                createdAt = 1747234800,
                updatedAt = 1747234800,
            )
        )
    }
}
