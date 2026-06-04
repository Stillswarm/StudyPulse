package com.studypulse.feat.flashcards.presentation.fcp_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
import com.studypulse.feat.flashcards.domain.FlashcardDataSignal
import com.studypulse.feat.flashcards.domain.FlashcardTopic
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import com.studypulse.feat.flashcards.domain.repository.UserStarsRepository
import com.studypulse.feat.flashcards.domain.usecase.DeleteFlashcardPackUseCase
import com.studypulse.feat.flashcards.domain.usecase.GetFlashcardPackForPresentation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardPackDetailsScreenViewModel(
    private val fcRepository: FlashcardRepository,
    private val userStarsRepository: UserStarsRepository,
    private val getFlashcardPackForPresentation: GetFlashcardPackForPresentation,
    private val deleteFlashcardPackUseCase: DeleteFlashcardPackUseCase,
    private val auth: FirebaseAuth,
    private val signal: FlashcardDataSignal,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val DEFAULT_FC_FETCH_COUNT = 20L
        private val WATCHED_TOPICS = arrayOf(
            FlashcardTopic.PACKS,
            FlashcardTopic.CARDS,
            FlashcardTopic.REVIEWS,
            FlashcardTopic.STARS,
        )
    }

    private val initialData = FlashcardPackDetailsScreenState()
    private val _state = MutableStateFlow(initialData)
    val state = _state.asStateFlow()
    val packId = savedStateHandle.get<String>("id")
    private var loadedAtVersion = -1L

    /** Lifecycle hook: only reload when this pack's data changed since last load. */
    fun refreshIfStale() {
        if (signal.versionOf(*WATCHED_TOPICS) == loadedAtVersion) return
        refresh()
    }

    fun refresh() {
        if (packId == null || _state.value.isRefreshing) return
        val versionAtStart = signal.versionOf(*WATCHED_TOPICS)
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            getFlashcardPackForPresentation(packId).onSuccess { fcp ->
                val currentUid = auth.uid
                _state.update {
                    it.copy(
                        fcp = fcp,
                        canDelete = currentUid != null && fcp.ownerId == currentUid,
                    )
                }
            }.onFailure {
                Log.d("app", "fcpRepository.getById(id): ${it.message}")
            }

            fcRepository.getNRandomFromSamePack(
                DEFAULT_FC_FETCH_COUNT,
                packId = packId,
            ).onSuccess { fcPage ->
                _state.update { it.copy(flashcardPage = fcPage) }
            }.onFailure { e ->
                Log.e("app", "flashcard pack details: getNRandomFromPack()", e)
            }

            loadedAtVersion = versionAtStart
            _state.update { it.copy(isRefreshing = false) }
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
