package com.studypulse.app.feat.attendance.attendance.domain.use_cases

import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
                if (courses.isEmpty()) {
                    flowOf(emptyMap())
                } else {
                    val summaryFlows = courses.map { course ->
                        courseSummaryRepository.getFlow(course.id)
                            .map { summary -> course to summary }
                    }

                    combine(summaryFlows) { summaries ->
                        val resultMap = mutableMapOf<Course, CourseSummary>()
                        summaries.forEach { (course, summary) ->
                            summary?.let {
                                resultMap[course] = it
                            }
                        }
                        resultMap
                    }
                }
            }
            .flowOn(Dispatchers.IO)
    }
}