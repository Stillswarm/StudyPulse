package com.studypulse.app.feat.user.presentation

import com.studypulse.app.feat.semester.domain.model.Semester
import com.studypulse.app.feat.user.domain.model.User

data class ProfileScreenState(
    val user: User? = null,
    val editingName: Boolean = false,
    val editingInstitution: Boolean = false,
    val currentName: String? = null,
    val currentInstitution: String? = null,
    val isLoading: Boolean = false,
    val currentSemester: Semester? = null,
    val semesterList: List<Semester> = emptyList()
)
