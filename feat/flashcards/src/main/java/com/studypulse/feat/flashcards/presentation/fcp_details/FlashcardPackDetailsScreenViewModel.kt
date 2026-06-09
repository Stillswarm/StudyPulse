package com.studypulse.feat.flashcards.presentation.fcp_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.repository.UserStarsRepository
import com.studypulse.feat.flashcards.domain.usecase.DeleteFlashcardPackUseCase
import com.studypulse.feat.flashcards.domain.usecase.GetFlashcardPackForPresentation
import com.studypulse.feat.flashcards.domain.usecase.GetPackCardsUseCase
import com.studypulse.feat.flashcards.domain.usecase.GetReviewQueueUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardPackDetailsScreenViewModel(
    private val getReviewQueue: GetReviewQueueUseCase,
    private val getPackCards: GetPackCardsUseCase,
    private val userStarsRepository: UserStarsRepository,
    private val getFlashcardPackForPresentation: GetFlashcardPackForPresentation,
    private val deleteFlashcardPackUseCase: DeleteFlashcardPackUseCase,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val DEFAULT_FC_FETCH_COUNT = 20L
    }

    private val initialData = FlashcardPackDetailsScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val packId = savedStateHandle.get<String>("id")

    init {
        refresh()
    }

    fun refresh() {
        if (packId == null || _state.value.isRefreshing) return
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val currentUid = auth.uid
            var isOwner = false
            getFlashcardPackForPresentation(packId).onSuccess { fcp ->
                isOwner = currentUid != null && fcp.ownerId == currentUid
                _state.update {
                    it.copy(
                        fcp = fcp,
                        isOwner = isOwner,
                        canDelete = isOwner,
                    )
                }
            }.onFailure {
                Log.d("app", "fcpRepository.getById(id): ${it.message}")
            }

            loadCards(packId, isOwner)

            _state.update { it.copy(isRefreshing = false) }
        }
    }

    /**
     * Owners see their scheduled study queue (ordered by SM-2 due date). For a
     * public pack owned by someone else, the cards live under the owner's
     * subtree, so we read them via a collection-group query and pair each with
     * the current user's own review state — which may well exist if they've
     * already reviewed cards from this pack. Only genuinely untouched cards fall
     * back to a freshly seeded state.
     */
    private suspend fun loadCards(packId: String, isOwner: Boolean) {
        val result = if (isOwner) {
            getReviewQueue(n = DEFAULT_FC_FETCH_COUNT, packId = packId).map { it.cards }
        } else {
            getPackCards(packId)
        }

        result.onSuccess { cards ->
            _state.update { it.copy(flashcardPage = FlashcardPage(cards = cards)) }
        }.onFailure { e ->
            Log.e("app", "flashcard pack details: loadCards(isOwner=$isOwner)", e)
        }
    }

    fun onStarIconClick() {
        val fcp = state.value.fcp ?: return
        if (packId == null) return

        val wasStarred = fcp.isStarredByUser
        _state.update {
            it.copy(
                fcp = fcp.copy(
                    isStarredByUser = !wasStarred,
                    starCount = (fcp.starCount + if (wasStarred) -1 else 1).coerceAtLeast(0),
                )
            )
        }

        viewModelScope.launch {
            val result = if (wasStarred) {
                userStarsRepository.unstarPack(packId)
            } else {
                userStarsRepository.starPack(packId)
            }
            result.onFailure { e ->
                Log.e("app", "star toggle failed", e)
                _state.update { s ->
                    val current = s.fcp ?: return@update s
                    s.copy(
                        fcp = current.copy(
                            isStarredByUser = wasStarred,
                            starCount = fcp.starCount,
                        )
                    )
                }
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
        val pack = _state.value.fcp ?: return
        if (!_state.value.canDelete || _state.value.isDeleting) return
        _state.update { it.copy(showDeleteDialog = false, isDeleting = true) }
        viewModelScope.launch {
            deleteFlashcardPackUseCase(pack)
                .onSuccess {
                    SnackbarController.sendEvent(SnackbarEvent("Pack deleted"))
                    _state.update { it.copy(isDeleting = false, deleted = true) }
                }
                .onFailure { e ->
                    Log.e("app", "Failed to delete pack ${pack.id}", e)
                    SnackbarController.sendEvent(
                        SnackbarEvent("Failed to delete pack: ${e.message}")
                    )
                    _state.update { it.copy(isDeleting = false) }
                }
        }
    }
}
