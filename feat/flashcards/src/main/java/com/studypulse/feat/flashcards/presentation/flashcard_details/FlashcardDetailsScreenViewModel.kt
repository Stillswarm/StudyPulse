package com.studypulse.feat.flashcards.presentation.flashcard_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.common.event.SnackbarController
import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardReviewState
import com.studypulse.feat.flashcards.domain.model.afterReview
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardReviewRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

// if there are cards, this is a study session -> we show left/right arrows here
// if not, this is a plain flashcard details view -> can edit here

internal class FlashcardDetailsScreenViewModel(
    val fcRepository: FlashcardRepository,
    val frRepository: FlashcardReviewRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialData = FlashcardDetailsScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val id = savedStateHandle.get<String>("id")
    val packId = savedStateHandle.get<String>("packId")
    val isEditing = savedStateHandle.get<Boolean>("isEditing")

    val cardsJson = savedStateHandle.get<String>("sm2cardsJson")
    val cards = if (cardsJson != null) Json.decodeFromString<List<Sm2Flashcard>>(cardsJson) else null
    val index = 0
    val canEdit = cards == null

    init {
        if (packId != null) {
            _state.update { it.copy(editing = isEditing ?: false) }
            if (id == null) {
                _state.update {
                    it.copy(
                        loading = false,
                        editing = true,
                        sm2fc = Sm2Flashcard(
                            flashcard = Flashcard(
                                id = "",
                                question = "",
                                answer = "",
                                description = null,
                                packId = packId,
                                ownerId = "",
                            ),
                            reviewState = FlashcardReviewState()
                        ),
                    )
                }
            } else {
                loadInitialFc()
            }
        } else if (cards != null) {
            _state.update { it.copy(sm2fc = cards[0]) }
        }
    }

    fun loadInitialFc() {
        if (id == null) return
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }

            val sm2 = async { frRepository.get(id) }
            val fc = async { fcRepository.getById(id) }

            val sm2fc = Sm2Flashcard(
                flashcard = fc.await().getOrElse
                {
                    SnackbarController.plainText("flashcard fetch failed")
                    return@launch
                },
                reviewState = sm2.await()
                    .getOrElse { FlashcardReviewState() }
            )

            _state.update { it.copy(sm2fc = sm2fc, loading = false) }
        }
    }

    fun updateQuestion(new: String) {
        state.value.sm2fc?.let { sm2fc ->
            _state.update { it.copy(sm2fc = sm2fc.copy(flashcard = sm2fc.flashcard.copy(question = new))) }
        }
    }

    fun updateAnswer(new: String) {
        state.value.sm2fc?.let { sm2fc ->
            _state.update { it.copy(sm2fc = sm2fc.copy(flashcard = sm2fc.flashcard.copy(answer = new))) }
        }
    }

    fun updateDescription(new: String) {
        state.value.sm2fc?.let { sm2fc ->
            _state.update { it.copy(sm2fc = sm2fc.copy(flashcard = sm2fc.flashcard.copy(description = new))) }
        }
    }

    fun submitEdit() {
        viewModelScope.launch {
            state.value.sm2fc?.let { fc ->
                fcRepository.upsert(fc.flashcard)
            }
        }
    }

    fun toggleEditing() {
        if (!canEdit) _state.update { it.copy(editing = false) }
        else _state.update { it.copy(editing = !state.value.editing) }
    }

    fun submitFeedback(score: Int) {
        _state.value.sm2fc?.let { fc ->
            viewModelScope.launch {
                frRepository.upsert(fc.afterReview(score).reviewState)
            }
        }
    }
}