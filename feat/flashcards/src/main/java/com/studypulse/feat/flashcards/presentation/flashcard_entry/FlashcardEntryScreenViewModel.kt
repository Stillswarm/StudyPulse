package com.studypulse.feat.flashcards.presentation.flashcard_entry

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.common.event.SnackbarController
import com.studypulse.common.event.SnackbarEvent
import com.studypulse.feat.flashcards.domain.model.FlashcardPack
import com.studypulse.feat.flashcards.domain.repository.FlashcardPackRepository
import com.studypulse.feat.flashcards.domain.repository.FlashcardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardEntryScreenViewModel(
    private val fcRepository: FlashcardRepository,
    private val fcpRepository: FlashcardPackRepository,
) : ViewModel() {

    companion object {
        const val CAROUSEL_CARDS_LIMIT = 20
        const val INITIAL_PACK_LIMIT = 5L
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getPopularPacks()
        }
        viewModelScope.launch(Dispatchers.IO) {
            getUserPacks()
        }
    }

    private val initialState = FlashcardEntryScreenState()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<FlashcardEntryScreenState> = _state.asStateFlow()


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

    fun fetchUsersRandomCards(offset: Int, limit: Int = CAROUSEL_CARDS_LIMIT) {

    }

    suspend fun getPopularPacks(limit: Long = INITIAL_PACK_LIMIT) {
        fcpRepository.getPopularPacks(limit)
            .onSuccess { packList ->
                _state.update { it.copy(popularPacks = packList) }
                Log.d("fcuk", packList.toString())
            }.onFailure {
                Log.e("app", "${it.printStackTrace()}")
            }
    }

    suspend fun getUserPacks(limit: Long = INITIAL_PACK_LIMIT) {
        fcpRepository.getNForThisUser(limit)
            .onSuccess { packList ->
                _state.update { it.copy(userPacks = packList) }
                Log.d("fcuk", packList.toString())
            }.onFailure {
                Log.e("app", "${it.printStackTrace()}")
            }
    }


}