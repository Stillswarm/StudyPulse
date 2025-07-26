package com.studypulse.app.feat.feedback.data

interface FeedbackRepository {
    suspend fun send(message: String): Result<Unit>
}