package com.studypulse.app.feat.attendance.attendance.domain.use_cases

import android.util.Log
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface GetCourseWiseSummariesUseCase {
    operator fun invoke(): Flow<Map<Course, CourseSummary>>
}

class GetCourseWiseSummariesUseCaseImpl(
    private val courseRepository: CourseRepository,
    private val courseSummaryRepository: CourseSummaryRepository,
) : GetCourseWiseSummariesUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<Map<Course, CourseSummary>> {
        return courseRepository.getAllCoursesFlow()
            .flatMapLatest { courses ->
                flow {
                    if (courses.isEmpty()) {
                        emit(emptyMap())
                    } else {
                        val map = mutableMapOf<Course, CourseSummary>()
                        for (course in courses) {
                            try {
                                val result = courseSummaryRepository.get(course.id)
                                result.getOrNull()?.let { summary ->
                                    map[course] = summary
                                }
                            } catch (e: Exception) {
                                Log.e("CourseSummaryFlow", "error fetching summary: ${e.message}")
                            }
                        }
                        // Emit once after all fetches are done
                        emit(map)
                    }
                }
                    .flowOn(Dispatchers.IO)
            }
    }
}