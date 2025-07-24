package com.studypulse.app.feat.semester.domain

import com.studypulse.app.feat.semester.domain.model.SemesterSummary
import kotlinx.coroutines.flow.Flow

interface SemesterSummaryRepository {
    suspend fun put(minAttendance: Int): Result<Unit>
    suspend fun get(): Result<SemesterSummary>
    suspend fun incPresent(by: Long): Result<Unit>
    suspend fun decPresent(by: Long): Result<Unit>
    suspend fun incAbsent(by: Long): Result<Unit>
    suspend fun decAbsent(by: Long): Result<Unit>
    suspend fun incUnmarked(by: Long): Result<Unit>
    suspend fun decUnmarked(by: Long): Result<Unit>
    suspend fun incCancelled(by: Long): Result<Unit>
    suspend fun decCancelled(by: Long): Result<Unit>

     fun getSummaryFlow(): Flow<SemesterSummary>
}