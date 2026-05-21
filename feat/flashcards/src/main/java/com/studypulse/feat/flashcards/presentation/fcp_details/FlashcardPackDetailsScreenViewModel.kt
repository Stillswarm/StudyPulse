package com.studypulse.feat.flashcards.presentation.fcp_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.feat.flashcards.domain.model.Flashcard
import com.studypulse.feat.flashcards.domain.model.FlashcardCursors
import com.studypulse.feat.flashcards.domain.model.FlashcardPage
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardPackDetailsScreenViewModel(
    private val fcRepository: FlashcardRepository,
    private val fcpRepository: FlashcardPackRepository,
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
        fetchInitialData()
    }

    fun fetchInitialData() {
        if (packId == null) return
        viewModelScope.launch {
            fcpRepository.getById(packId).onSuccess { fcp ->
                _state.update { it.copy(fcp = fcp) }
            }.onFailure {
                Log.d("app", "fcpRepository.getById(id): ${it.message}")
            }
        }

        fetchCards()
    }

    fun fetchCards() {
        if (packId == null) return
        viewModelScope.launch {
            val cursors = _state.value.flashcardPage.cursors
            fcRepository.getNRandomFromSamePack(
                DEFAULT_FC_FETCH_COUNT,
                packId = packId,
                cursors = cursors,
            ).onSuccess { fcPage ->
                _state.update {
                    it.copy(
                        flashcardPage = FlashcardPage(
                            cards = it.flashcardPage.cards + fcPage.cards,
                            cursors = fcPage.cursors,
                        )
                    )
                }
            }.onFailure { e ->
                Log.e("app", "flashcard pack details: getNRandomFromPack()", e)
            }
        }
    }
}