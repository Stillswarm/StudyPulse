package com.studypulse.feat.flashcards.presentation.flashcard_entry

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
import com.studypulse.feat.flashcards.ReviewCache
import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.model.afterReview
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.usecase.GetReviewQueueUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardEntryScreenViewModel(
    private val getReviewQueue: GetReviewQueueUseCase,
    private val fcpRepository: FlashcardPackRepository,
    private val reviewCache: ReviewCache,
) : ViewModel() {

    companion object {
        const val INITIAL_PACK_LIMIT = 5L
    }

    private val initialState = FlashcardEntryScreenState()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<FlashcardEntryScreenState> = _state.asStateFlow()

    init {
        // One-time, idempotent migration so existing cards carry their pack's
        // `public` flag (required for cross-owner public reads). No-op after
        // the first successful run.
        viewModelScope.launch {
            fcpRepository.backfillPublicFlags().onFailure {
                Log.e("app", "flashcard public backfill failed", it)
            }
        }
    }

    fun refresh() {
        if (_state.value.isRefreshing) return
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            joinAll(
                launch(Dispatchers.IO) { getPopularPacks() },
                launch(Dispatchers.IO) { getUserPacks() },
                launch(Dispatchers.IO) { loadRandomCards() },
            )
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    // The entry screen loads a single fixed-size batch of due cards for quick
    // revision; deeper study continues in the dedicated study session.
    private suspend fun loadRandomCards() {
        getReviewQueue(n = INITIAL_PACK_LIMIT).onSuccess { page ->
            _state.update { it.copy(quickRevisionPage = page) }
        }
    }

    fun onNewFcpTitleChange(newTitle: String) {
        _state.update { it.copy(newFcp = it.newFcp.copy(title = newTitle)) }
    }

    fun onNewFcpDescriptionChange(newDescription: String) {
        _state.update { it.copy(newFcp = it.newFcp.copy(description = newDescription)) }
    }

    fun onNewFcpColorChange(newColor: Color) {
        _state.update { it.copy(newFcp = it.newFcp.copy(color = newColor)) }
    }

    fun onNewFcpVisibilityToggle(new: Boolean) {
        _state.update { it.copy(newFcp = it.newFcp.copy(public = new)) }
    }

    fun resetNewFcpState() {
        _state.update { it.copy(newFcp = FlashcardPack(title = "")) }
    }

    fun addNewPackAndNavigate(onNavigateToFcpScreen: (id: String) -> Unit) =
        viewModelScope.launch {
            fcpRepository.upsert(_state.value.newFcp).onFailure {
                SnackbarController.sendEvent(SnackbarEvent("Failed to create flashcard pack!"))
            }.onSuccess { id ->
                resetNewFcpState()
                onNavigateToFcpScreen(id)
            }.onFailure {
                Log.e("app", "${it.printStackTrace()}")
            }
        }

    suspend fun getPopularPacks(limit: Long = INITIAL_PACK_LIMIT) {
        fcpRepository.getPopularPacks(limit)
            .onSuccess { page ->
                _state.update { it.copy(popularPacks = page.items) }
            }.onFailure {
                Log.e("app", "${it.printStackTrace()}")
            }
    }

    suspend fun getUserPacks(limit: Long = INITIAL_PACK_LIMIT) {
        fcpRepository.getNForThisUser(limit)
            .onSuccess { page ->
                _state.update { it.copy(userPacks = page.items) }
            }.onFailure {
                Log.e("app", "${it.printStackTrace()}")
            }
    }

    fun onCardFeedback(sm2fc: Sm2Flashcard, q: Int) {
        val newReviewState = sm2fc.afterReview(q).reviewState.copy(
            cardId = sm2fc.flashcard.id,
            packId = sm2fc.flashcard.packId,
        )
        reviewCache.append(newReviewState)
    }


}
