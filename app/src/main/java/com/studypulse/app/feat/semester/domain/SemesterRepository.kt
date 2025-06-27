package com.studypulse.app.feat.semester.domain

import com.studypulse.app.feat.semester.domain.model.Semester

interface SemesterRepository {
    suspend fun addSemester(semester: Semester): Result<Unit>
    suspend fun markCurrent(semesterId: String): Result<Unit>
    suspend fun markCompleted(semesterId: String): Result<Unit>
    suspend fun getActiveSemester(): Result<Semester?>
    suspend fun getAllSemesters(): Result<List<Semester>>
}