package com.studypulse.feat.flashcards.presentation.fcp_details

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.common.R
import com.studypulse.common.utils.DateUtils.toFullString
import com.studypulse.common.utils.DateUtils.toLocalDate
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.ui.components.AppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.GreenSecondary
import com.studypulse.ui.theme.Orange
import com.studypulse.ui.theme.Purple
import com.studypulse.ui.theme.Red
import com.studypulse.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel
import com.studypulse.feat.flashcards.R as FcR

@Composable
fun FlashcardPackDetailsScreen(
    navigateToFcDetails: (id: String?, packId: String, editing: Boolean) -> Unit,
    onBack: () -> Unit = {},
    onStudy: () -> Unit = {},
    modifier: Modifier = Modifier,
    vm: FlashcardPackDetailsScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val pack = state.fcp
    val flashcards = state.flashcardPage.cards

    Box(modifier = modifier.fillMaxSize()) {
        if (pack == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Cyan)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 116.dp,
                    bottom = 120.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    PackHeroCard(
                        pack = pack,
                        cardCount = flashcards.size,
                        onStudy = onStudy,
                    )
                }

                item {
                    Text(
                        text = "Cards (${flashcards.size})",
                        style = Typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkGray,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                if (flashcards.isEmpty()) {
                    item { EmptyCardsState() }
                } else {
                    items(items = flashcards, key = { it.id }) { fc ->
                        FlashcardListItem(
                            flashcard = fc,
                            onClick = { navigateToFcDetails(fc.id, fc.packId, false) },
                        )
                    }
                }
            }
        }

        AppTopBar(
            backgroundColor = Cyan,
            foregroundGradient = null,
            title = "Pack details",
            titleColor = Color.White,
            navigationIcon = FcR.drawable.ic_arrow_left,
            onNavigationClick = onBack,
            actionIcon = null,
            onActionClick = null,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        if (pack != null) {
            FloatingActionButton(
                onClick = { navigateToFcDetails(null, pack.id, true) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .size(60.dp),
                shape = RoundedCornerShape(18.dp),
                containerColor = Cyan,
                contentColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new card",
                    modifier = Modifier.size(28.dp),
                )
            }
        }
    }
}

@Composable
private fun PackHeroCard(
    pack: FlashcardPack,
    cardCount: Int,
    onStudy: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(pack.color),
            )
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = pack.title,
                        style = Typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.width(8.dp))
                    VisibilityChip(isPublic = pack.isPublic)
                }

                pack.description?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        style = Typography.bodyMedium,
                        color = DarkGray.copy(alpha = 0.75f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatChip(text = "$cardCount cards")
                    StatChip(
                        text = "34",
                        leadingIcon = R.drawable.ic_filled_star,
                    )
                    StatChip(
                        text = "Updated ${pack.updatedAt.toLocalDate().toFullString()}",
                        modifier = Modifier.weight(1f),
                    )
                }

                Button(
                    onClick = onStudy,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Cyan),
                    enabled = cardCount > 0,
                ) {
                    Text(
                        text = if (cardCount > 0) "Study" else "Add cards to study",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Cyan.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                tint = Cyan,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text = text,
            style = Typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun VisibilityChip(
    isPublic: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, Cyan, CircleShape)
            .background(Cyan.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = if (isPublic) "PUBLIC" else "PRIVATE",
            style = Typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Cyan,
        )
    }
}

@Composable
private fun FlashcardListItem(
    flashcard: Flashcard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val status = remember(flashcard) { flashcard.toStatus() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Cyan.copy(alpha = 0.25f),
                shape = RoundedCornerShape(12.dp),
            )
            .noRippleClickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = flashcard.question,
                style = Typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(8.dp))
            StatusChip(status = status)
        }
        Text(
            text = flashcard.answer,
            style = Typography.bodySmall,
            color = DarkGray.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private enum class CardStatus(val label: String, val color: Color) {
    New("NEW", Purple),
    Learning("LEARNING", Orange),
    Due("DUE", Red),
    Reviewing("REVIEWING", GreenSecondary),
}

private fun Flashcard.toStatus(): CardStatus {
    val now = System.currentTimeMillis()
    return when {
        n == 0 -> CardStatus.New
        dueDate <= now -> CardStatus.Due
        n in 1..2 -> CardStatus.Learning
        else -> CardStatus.Reviewing
    }
}

@Composable
private fun StatusChip(
    status: CardStatus,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(status.color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = status.label,
            style = Typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = status.color,
        )
    }
}

@Composable
private fun EmptyCardsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
            text = "No cards yet",
            style = Typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = DarkGray,
        )
        Text(
            text = "Tap + to create your first flashcard",
            style = Typography.bodyMedium,
            color = DarkGray.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}
