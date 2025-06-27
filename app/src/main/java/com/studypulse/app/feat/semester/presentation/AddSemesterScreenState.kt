package com.studypulse.app.feat.semester.presentation

import com.studypulse.app.feat.semester.domain.model.SemesterName
import java.time.LocalDate

data class AddSemesterScreenState(
    val name: SemesterName? = null,
    val year: Int? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)
