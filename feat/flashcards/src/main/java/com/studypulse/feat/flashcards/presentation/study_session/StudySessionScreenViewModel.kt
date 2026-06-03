package com.studypulse.feat.flashcards.presentation.study_session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.feat.flashcards.ReviewCache
import com.studypulse.feat.flashcards.data.Sm2Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.model.afterReview
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class StudySessionScreenViewModel(
    private val fcRepository: FlashcardRepository,
    private val reviewCache: ReviewCache,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 20L
        const val PREFETCH_THRESHOLD = 5
    }

    private val packId: String? = savedStateHandle.get<String>("packId")
    private val mutex = Mutex()

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
                val cursors = _state.value.page.cursors
                val result = if (packId != null) {
                    fcRepository.getNRandomFromSamePack(PAGE_SIZE, packId, cursors)
                } else {
                    fcRepository.getNRandomFromAcrossPacks(PAGE_SIZE, cursors)
                }
                result.onSuccess { newPage ->
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

    fun onCardFeedback(sm2: Sm2Flashcard, q: Int) {
        val newReviewState = sm2.afterReview(q).reviewState.copy(cardId = sm2.flashcard.id)
        reviewCache.append(newReviewState)
    }
}
