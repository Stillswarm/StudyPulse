package com.studypulse.feat.flashcards.presentation.flashcard_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
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

internal class FlashcardDetailsScreenViewModel(
    val fcRepository: FlashcardRepository,
    val frRepository: FlashcardReviewRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialData = FlashcardDetailsScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val id = savedStateHandle.get<String>("id")
    val packId = savedStateHandle.get<String>("packId")
    val isEditing = savedStateHandle.get<Boolean>("isEditing")

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

            _state.update {
                it.copy(
                    sm2fc = sm2fc,
                    loading = false,
                    canDelete = isOwner(sm2fc),
                )
            }
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
        _state.update { it.copy(editing = !state.value.editing) }
    }

    fun submitFeedback(score: Int) {
        _state.value.sm2fc?.let { fc ->
            viewModelScope.launch {
                frRepository.upsert(fc.afterReview(score).reviewState)
            }
        }
    }

    fun onDeleteClick() {
        if (!_state.value.canDelete) return
        _state.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteDismiss() {
        _state.update { it.copy(showDeleteDialog = false) }
    }

    fun onDeleteConfirm() {
        val sm2 = _state.value.sm2fc ?: return
        if (!_state.value.canDelete || _state.value.isDeleting) return
        _state.update { it.copy(showDeleteDialog = false, isDeleting = true) }
        viewModelScope.launch {
            fcRepository.delete(sm2.flashcard)
                .onSuccess {
                    SnackbarController.sendEvent(SnackbarEvent("Card deleted"))
                    _state.update { it.copy(isDeleting = false, deleted = true) }
                }
                .onFailure { e ->
                    Log.e("app", "Failed to delete card ${sm2.flashcard.id}", e)
                    SnackbarController.sendEvent(
                        SnackbarEvent("Failed to delete card: ${e.message}")
                    )
                    _state.update { it.copy(isDeleting = false) }
                }
        }
    }

    private fun isOwner(sm2: Sm2Flashcard): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return sm2.flashcard.ownerId.isNotBlank() && sm2.flashcard.ownerId == uid
    }
}
