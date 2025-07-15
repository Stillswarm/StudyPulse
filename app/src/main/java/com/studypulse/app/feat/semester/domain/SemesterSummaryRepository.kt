package com.studypulse.app.feat.semester.domain

import com.studypulse.app.feat.semester.domain.model.SemesterSummary

interface SemesterSummaryRepository {
    suspend fun put(): Result<Unit>
    suspend fun get(): Result<SemesterSummary>
    suspend fun incPresent(by: Int): Result<Unit>
    suspend fun decPresent(by: Int): Result<Unit>
    suspend fun incAbsent(by: Int): Result<Unit>
    suspend fun decAbsent(by: Int): Result<Unit>
    suspend fun incUnmarked(by: Int): Result<Unit>
    suspend fun decUnmarked(by: Int): Result<Unit>
    suspend fun incCancelled(by: Int): Result<Unit>
    suspend fun decCancelled(by: Int): Result<Unit>
}