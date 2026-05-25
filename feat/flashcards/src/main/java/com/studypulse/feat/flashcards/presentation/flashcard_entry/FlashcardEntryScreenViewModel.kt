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
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.model.afterReview
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class FlashcardEntryScreenViewModel(
    private val fcRepository: FlashcardRepository,
    private val fcpRepository: FlashcardPackRepository,
    private val reviewCache: ReviewCache,
) : ViewModel() {

    companion object {
        const val CAROUSEL_CARDS_LIMIT = 20
        const val INITIAL_PACK_LIMIT = 5L
    }

    private val initialState = FlashcardEntryScreenState()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<FlashcardEntryScreenState> = _state.asStateFlow()

    private val mutex = Mutex()
    private var allFetched = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getPopularPacks()
        }
        viewModelScope.launch(Dispatchers.IO) {
            getUserPacks()
        }
        getRandomCards()
    }

    /** this was the earlier logic to keep fetching cards
    // as the available cards neared exhaustion

    // eventually it was decided that only a fixed number of cards
    // will be loaded for entry screen. beyond that, the user can continue
    // studying by entering a dedicated study session **/

    /*fun getRandomCards() {
        if (allFetched) return
        viewModelScope.launch(Dispatchers.IO) {
            if (!mutex.tryLock()) return@launch
            try {
                fcRepository.getNRandomFromAcrossPacks(
                    CAROUSEL_CARDS_LIMIT.toLong(),
                    cursors = _state.value.quickRevisionPage.cursors
                )
                    .onSuccess { newPage ->
                        if (newPage.cards.isEmpty()) {
                            allFetched = true
                            return@onSuccess
                        }
                        _state.update {
                            it.copy(
                                quickRevisionPage = FlashcardPage(
                                    cards = it.quickRevisionPage.cards + newPage.cards,
                                    cursors = newPage.cursors
                                )
                            )
                        }
                    }
            } finally {
                mutex.unlock()
            }
        }
    }*/

    fun getRandomCards() {
        viewModelScope.launch(Dispatchers.IO) {
            fcRepository.getNRandomFromAcrossPacks(INITIAL_PACK_LIMIT).onSuccess { page ->
                _state.update { it.copy(quickRevisionPage = page) }
            }
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
        _state.update { it.copy(newFcp = it.newFcp.copy(isPublic = new)) }
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
        val newReviewState = sm2fc.afterReview(q).reviewState.copy(cardId = sm2fc.flashcard.id)
        Log.d("app", "new review state card id = ${newReviewState.cardId}")

        reviewCache.append(newReviewState)
    }


}