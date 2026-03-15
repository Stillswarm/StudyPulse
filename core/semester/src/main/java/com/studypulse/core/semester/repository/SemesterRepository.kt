package com.studypulse.core.semester.repository

import com.studypulse.core.semester.model.Semester

interface SemesterRepository {
    suspend fun addActiveSemester(semester: Semester): Result<Unit>
    suspend fun markCurrent(semesterId: String): Result<Unit>
    suspend fun markCompleted(semesterId: String): Result<Unit>
    suspend fun getActiveSemester(): Result<Semester?>
    suspend fun getAllSemesters(): Result<List<Semester>>
}
