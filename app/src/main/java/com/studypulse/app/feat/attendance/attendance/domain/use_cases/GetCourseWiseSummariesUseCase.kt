package com.studypulse.app.feat.attendance.attendance.domain.use_cases

import android.util.Log
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.attendance.courses.domain.model.Course
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

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
                    callbackFlow {
                        trySend(emptyMap())
                        awaitClose {}
                    }
                } else {
                    callbackFlow {
                        val job = CoroutineScope(EmptyCoroutineContext).launch {
                            val map = mutableMapOf<Course, CourseSummary>()
                            for (course in courses) {
                                try {
                                    val summaryResult = courseSummaryRepository.get(course.id)
                                    val summary = summaryResult.getOrNull()
                                    if (summary != null) {
                                        map[course] = summary
                                    }
                                } catch (e: Exception) {
                                    Log.e("tag", "error ${e.message}")
                                }
                            }
                            trySend(map)
                        }
                        awaitClose { job.cancel() }
                    }
                }
            }
    }
}