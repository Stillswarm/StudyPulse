package com.studypulse.feat.flashcards.presentation.fcp_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.model.PackPage
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.nav.routes.FcpListType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardPackListScreenViewModel(
    private val fcpRepository: FlashcardPackRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val type = savedStateHandle.get<FcpListType?>("type")

    private val fetchPage: suspend (Long, DocumentSnapshot?) -> Result<PackPage> =
        if (type == FcpListType.POPULAR) fcpRepository::getPopularPacks
        else fcpRepository::getNForThisUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _list = MutableStateFlow<List<FlashcardPack>>(emptyList())
    val list: StateFlow<List<FlashcardPack>> = _list.asStateFlow()

    // Pagination state lives on the VM so it is scoped to this screen's lifetime
    private var cursor: DocumentSnapshot? = null
    private var endReached = false

    init {
        viewModelScope.launch { getNextSet() }
    }

    suspend fun getNextSet() {
        if (_isLoading.value || endReached) return
        _isLoading.value = true
        try {
            fetchPage(PAGE_SIZE, cursor).onSuccess { page ->
                cursor = page.nextCursor ?: cursor
                endReached = page.endReached
                if (page.items.isNotEmpty()) {
                    _list.update { it + page.items }
                }
            }
        } finally {
            _isLoading.value = false
        }
    }

    companion object {
        private const val PAGE_SIZE = 5L
    }
}
