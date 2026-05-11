package com.studypulse.feat.flashcards.presentation.fcp_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.nav.routes.FcpListType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class FlashcardPackListScreenViewModel(
    private val fcpRepository: FlashcardPackRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val type = savedStateHandle.getStateFlow<FcpListType?>("type", null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val listFlow: Flow<List<FlashcardPack>> = type.flatMapLatest { type ->
        when (type) {
            null -> emptyFlow()
            FcpListType.USER -> fcpRepository.getAllForThisUserFlow().getOrNull() ?: emptyFlow()
            FcpListType.POPULAR -> fcpRepository.getAllForThisUserFlow().getOrNull() ?: emptyFlow()
        }
    }
    val listStateFlow: StateFlow<List<FlashcardPack>> = listFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}