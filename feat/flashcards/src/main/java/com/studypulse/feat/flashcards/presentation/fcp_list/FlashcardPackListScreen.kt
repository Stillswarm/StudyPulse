package com.studypulse.feat.flashcards.presentation.fcp_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.common.R
import com.studypulse.common.utils.DateUtils.toFullString
import com.studypulse.common.utils.DateUtils.toLocalDate
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.nav.routes.FcpListType
import com.studypulse.ui.components.AppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.Typography
import com.studypulse.ui.theme.WhiteSecondary
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
    val list by vm.listStateFlow.collectAsStateWithLifecycle()
    val type by vm.type.collectAsStateWithLifecycle()

    val title = when (type) {
        FcpListType.USER -> "Your Packs"
        FcpListType.POPULAR -> "Popular Packs"
        null -> "Packs"
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (list.isEmpty()) {
            EmptyPackListState(
                isUserList = type == FcpListType.USER,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
    val mildFcpColor = fcp.color.copy(alpha = 0.05f)
    val mildCyan = Cyan.copy(alpha = 0.5f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(mildCyan, mildFcpColor)
                )
            )
            .then(
                if (onClick != null) Modifier.noRippleClickable { onClick() } else Modifier
            )
    ) {
        Row {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(10.dp)
                    .background(color = fcp.color)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = fcp.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.5.sp,
                            color = DarkGray,
                        )
                        fcp.description?.let {
                            Text(
                                text = it.lowercase(),
                                maxLines = 2,
                                minLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = DarkGray.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .aspectRatio(2f)
                                .wrapContentSize()
                                .clip(CircleShape)
                                .border(1.dp, Cyan, CircleShape)
                                .background(Cyan.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (fcp.isPublic) "PUBLIC" else "PRIVATE",
                                fontSize = 8.sp,
                                color = DarkGray,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    StarWithCount(34)
                }

                Column {
                    Text(
                        text = "Last Updated: ${fcp.updatedAt.toLocalDate().toFullString()}",
                        color = DarkGray.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun StarWithCount(count: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(48.dp)
            .height(20.dp)
            .clip(CircleShape)
            .background(WhiteSecondary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_filled_star),
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.size(24.dp)
            )

            Text(text = count.toString(), fontSize = 10.sp)
        }
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
                isPublic = true,
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
                isPublic = true,
                createdAt = 1747234800,
                updatedAt = 1747234800,
            )
        )
    }
}
