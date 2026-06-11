package com.studypulse.feat.flashcards.presentation.flashcard_details

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardFeedback
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.presentation.common.DeleteConfirmationDialog
import com.studypulse.feat.flashcards.presentation.flashcard_entry.FlashcardItem
import com.studypulse.ui.components.AppTopBar
import com.studypulse.ui.modifier.noRippleClickable
import com.studypulse.ui.theme.Cyan
import com.studypulse.ui.theme.DarkGray
import com.studypulse.ui.theme.Red
import com.studypulse.ui.theme.Typography
import com.studypulse.ui.theme.WarmWhite
import org.koin.androidx.compose.koinViewModel
import com.studypulse.feat.flashcards.R as FcR

@Composable
internal fun FlashcardDetailsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: FlashcardDetailsScreenViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isCreating = vm.id == null

    val title = when {
        isCreating -> "New Card"
        state.editing -> "Edit Card"
        else -> "Flashcard"
    }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
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
                title = title,
                navigationIcon = FcR.drawable.ic_arrow_left,
                onNavigationClick = onBack,
                actionIcon = when {
                    !state.canEdit -> null
                    state.editing -> FcR.drawable.ic_check
                    else -> FcR.drawable.ic_edit
                },
                onActionClick = if (state.canEdit) {
                    {
                        if (state.editing) {
                            vm.submitEdit()
                            vm.toggleEditing()
                        } else {
                            vm.toggleEditing()
                        }
                    }
                } else null,
                titleColor = Color.White,
            )

            when {
                state.loading -> CenteredMessage(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(color = Cyan)
                }

                state.sm2fc == null -> CenteredMessage(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = "We couldn't load this card.",
                        style = Typography.bodyMedium,
                        color = DarkGray.copy(alpha = 0.7f),
                    )
                }

                state.editing -> EditMode(
                    fc = state.sm2fc!!.flashcard,
                    onQuestionChange = vm::updateQuestion,
                    onAnswerChange = vm::updateAnswer,
                    onDescriptionChange = vm::updateDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                )

                else -> ViewMode(
                    fc = state.sm2fc!!,
                    canDelete = state.canDelete,
                    onDeleteClick = vm::onDeleteClick,
                    onFeedback = { fb -> vm.submitFeedback(fb.score) },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                )
            }
        }

        if (state.showDeleteDialog && state.sm2fc != null) {
            DeleteConfirmationDialog(
                title = "Delete card?",
                message = "This will permanently remove this flashcard along with its review history. This action cannot be undone.",
                confirmLabel = "Delete card",
                onConfirm = vm::onDeleteConfirm,
                onDismiss = vm::onDeleteDismiss,
            )
        }
    }
}

@Composable
private fun CenteredMessage(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        content()
    }
}

@Composable
private fun ViewMode(
    fc: Sm2Flashcard,
    canDelete: Boolean,
    onDeleteClick: () -> Unit,
    onFeedback: (FlashcardFeedback) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        FlashcardItem(
            fc = fc.flashcard,
            onFeedback = onFeedback,
            height = 300.dp,
        )

        if (!fc.flashcard.description.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SectionLabel(text = "DESCRIPTION")
                Text(
                    text = fc.flashcard.description,
                    style = Typography.bodyMedium,
                    color = DarkGray.copy(alpha = 0.85f),
                )
            }
        }

        StatsCard(reviewState = fc.reviewState)

        if (canDelete) {
            DeleteCardButton(onClick = onDeleteClick)
        }
    }
}

@Composable
private fun DeleteCardButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Red.copy(alpha = 0.6f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "Delete card",
                style = Typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
private fun StatsCard(
    reviewState: FlashcardReviewState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Cyan.copy(alpha = 0.10f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatCell(label = "Reviewed", value = "${reviewState.n}×")
        StatCell(label = "Interval", value = "${reviewState.interval}d")
        StatCell(label = "Ease", value = "%.2f".format(reviewState.ef))
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = Typography.labelSmall,
            color = DarkGray.copy(alpha = 0.65f),
        )
        Text(
            text = value,
            style = Typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DarkGray,
        )
    }
}

@Composable
private fun EditMode(
    fc: Flashcard,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        FlippableEditCard(
            question = fc.question,
            answer = fc.answer,
            onQuestionChange = onQuestionChange,
            onAnswerChange = onAnswerChange,
            height = 300.dp,
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionLabel(text = "DESCRIPTION (optional)")
            EditTextField(
                value = fc.description.orEmpty(),
                onValueChange = onDescriptionChange,
                placeholder = "Add a hint or extra context",
                minLines = 3,
            )
        }
    }
}

@Composable
private fun FlippableEditCard(
    question: String,
    answer: String,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
) {
    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "edit card flip"
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
                SectionLabel(
                    text = if (isShowingAnswer) "ANSWER" else "QUESTION",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    EditTextField(
                        value = if (isShowingAnswer) answer else question,
                        onValueChange = if (isShowingAnswer) onAnswerChange else onQuestionChange,
                        placeholder = if (isShowingAnswer) "What's the answer?" else "What's the question?",
                        minLines = 2,
                        textAlign = TextAlign.Center,
                    )
                }

                Text(
                    text = if (isShowingAnswer) "Flip to enter question" else "Flip to enter answer",
                    style = Typography.labelSmall,
                    color = Cyan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                        .noRippleClickable { flipped = !flipped },
                )
            }
        }
    }
}

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = Typography.bodyMedium,
                color = DarkGray.copy(alpha = 0.45f),
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        singleLine = false,
        minLines = minLines,
        textStyle = Typography.bodyLarge.copy(
            color = DarkGray,
            fontWeight = FontWeight.Medium,
            textAlign = textAlign ?: TextAlign.Start,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Cyan.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp),
            ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Cyan,
        ),
    )
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = Typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = DarkGray.copy(alpha = 0.65f),
        modifier = modifier,
    )
}

