package com.studypulse.feat.flashcards.presentation.study_session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.studypulse.feat.flashcards.ReviewCache
import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.model.afterReview
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.usecase.GetPackCardsUseCase
import com.studypulse.feat.flashcards.domain.usecase.GetReviewQueueUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class StudySessionScreenViewModel(
    private val getReviewQueue: GetReviewQueueUseCase,
    private val getPackCards: GetPackCardsUseCase,
    private val fcpRepository: FlashcardPackRepository,
    private val reviewCache: ReviewCache,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 20L
        const val PREFETCH_THRESHOLD = 5
    }

    private val packId: String? = savedStateHandle.get<String>("packId")
    private val mutex = Mutex()

    // Whether the current user owns this pack (and therefore has per-user SM-2
    // review states for it). Resolved lazily on first load. A null packId means
    // "study across my own packs", which is always owner-mode.
    private var ownsPack: Boolean? = if (packId == null) true else null

    private val _state = MutableStateFlow(StudySessionScreenState())
    val state = _state.asStateFlow()

    init {
        fetchNextPage(initial = true)
    }

    fun maybePrefetch(currentPage: Int) {
        val s = _state.value
        if (s.allFetched || s.isPaginating) return
        if (currentPage >= s.page.cards.size - PREFETCH_THRESHOLD) {
            fetchNextPage(initial = false)
        }
    }

    private fun fetchNextPage(initial: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!mutex.tryLock()) return@launch
            try {
                _state.update {
                    if (initial) it.copy(loading = true) else it.copy(isPaginating = true)
                }

                // Public packs the user doesn't own have no per-user review
                // states, so the SM-2 queue is empty for them. Fall back to the
                // full card set (loaded once, no pagination) so they can still
                // be studied; feedback still seeds the user's own review states.
                if (packId != null && !resolveOwnership()) {
                    if (initial) loadFullPack(packId)
                    else _state.update { it.copy(isPaginating = false, allFetched = true) }
                    return@launch
                }

                val cursors = _state.value.page.cursors
                getReviewQueue(PAGE_SIZE, packId, cursors).onSuccess { newPage ->
                    val isExhausted = newPage.cards.isEmpty()
                    _state.update {
                        it.copy(
                            page = FlashcardPage(
                                cards = it.page.cards + newPage.cards,
                                cursors = newPage.cursors,
                            ),
                            loading = false,
                            isPaginating = false,
                            allFetched = isExhausted,
                        )
                    }
                }.onFailure {
                    _state.update {
                        it.copy(loading = false, isPaginating = false)
                    }
                }
            } finally {
                mutex.unlock()
            }
        }
    }

    /** Resolves (and memoizes) whether the current user owns [packId]. */
    private suspend fun resolveOwnership(): Boolean {
        ownsPack?.let { return it }
        val uid = auth.currentUser?.uid
        val owns = uid != null &&
            fcpRepository.getById(packId!!).getOrNull()?.ownerId == uid
        ownsPack = owns
        return owns
    }

    private suspend fun loadFullPack(packId: String) {
        getPackCards(packId).onSuccess { cards ->
            _state.update {
                it.copy(
                    page = FlashcardPage(cards = cards),
                    loading = false,
                    isPaginating = false,
                    allFetched = true,
                )
            }
        }.onFailure {
            _state.update { it.copy(loading = false, isPaginating = false) }
        }
    }

    fun onCardFeedback(sm2: Sm2Flashcard, q: Int) {
        val newReviewState = sm2.afterReview(q).reviewState.copy(
            cardId = sm2.flashcard.id,
            packId = sm2.flashcard.packId,
        )
        reviewCache.append(newReviewState)
    }
}
