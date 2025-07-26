package com.studypulse.app.feat.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studypulse.app.SnackbarController
import com.studypulse.app.SnackbarEvent
import com.studypulse.app.feat.feedback.data.FeedbackRepository
import kotlinx.coroutines.launch

class FeedbackScreenViewModel(
    private val feedbackRepository: FeedbackRepository
) : ViewModel() {

    fun submitFeedback(message: String) {
        viewModelScope.launch {
            feedbackRepository.send(message).onSuccess {
                SnackbarController.sendEvent(SnackbarEvent("Feedback submitted"))
            }.onFailure {
                SnackbarController.sendEvent(SnackbarEvent("Failed to submit feedback"))
            }
        }
    }

}