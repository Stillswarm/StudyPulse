package com.studypulse.app.feat.attendance.attendance.domain.use_cases

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.studypulse.app.course1
import com.studypulse.app.course2
import com.studypulse.app.courseSummary1
import com.studypulse.app.courseSummary2
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.attendance.courses.domain.model.CourseDto
import com.studypulse.app.feat.attendance.courses.domain.model.CourseSummaryDto
import com.studypulse.app.feat.attendance.courses.domain.model.toDomain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetCourseWiseSummariesUseCaseImplTest {

    private lateinit var useCase: GetCourseWiseSummariesUseCaseImpl
    private lateinit var courseRepository: CourseRepository
    private lateinit var courseSummaryRepository: CourseSummaryRepository

    @Before
    fun setUp() {
        courseRepository = mockk<CourseRepository>()
        courseSummaryRepository = mockk<CourseSummaryRepository>()
        useCase = GetCourseWiseSummariesUseCaseImpl(courseRepository, courseSummaryRepository)

        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ======== HAPPY PATHS ========
    @Test
    fun `invoke - should return flow of map(course, courseSummary) on successful fetch`() =
        runTest {
            every { courseRepository.getAllCoursesFlow() } returns flowOf(listOf(course1, course2))
            coEvery { courseSummaryRepository.get("c1") } returns Result.success(courseSummary1)
            coEvery { courseSummaryRepository.get("c2") } returns Result.success(courseSummary2)

            useCase.invoke().test {
                val result = awaitItem()
                assertThat(result).hasSize(2)
                assertThat(result).containsEntry(course1, courseSummary1)
                assertThat(result).containsEntry(course2, courseSummary2)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke - should return flow of empty map when no courses or summaries are available`() =
        runTest {
            every { courseRepository.getAllCoursesFlow() } returns flowOf(emptyList())

            useCase.invoke().test {
                val result = awaitItem()
                assertThat(result).hasSize(0)
                assertThat(result).isEmpty()

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke - should emit new data when course flow updates`() =
        runTest {
            every { courseRepository.getAllCoursesFlow() } returns flowOf(
                listOf(course1),
                listOf(course1, course2)
            )
            coEvery { courseSummaryRepository.get("c1") } returns Result.success(courseSummary1)
            coEvery { courseSummaryRepository.get("c2") } returns Result.success(courseSummary2)

            useCase.invoke().test {
                val initial = awaitItem()

                assertThat(initial).hasSize(1)
                assertThat(initial).containsEntry(course1, courseSummary1)
                assertThat(initial).doesNotContainKey(course2)

                val updated = awaitItem()

                assertThat(updated).hasSize(2)
                assertThat(updated).containsEntry(course1, courseSummary1)
                assertThat(updated).containsEntry(course2, courseSummary2)

                cancelAndIgnoreRemainingEvents()
            }
        }


    // ======== ERROR PATHS ========
    @Test
    fun `invoke - should only return summaries that were successfully fetched`() =
        runTest {
            every { courseRepository.getAllCoursesFlow() } returns flowOf(listOf(course1, course2))
            coEvery { courseSummaryRepository.get("c1") } returns Result.failure(Exception())
            coEvery { courseSummaryRepository.get("c2") } returns Result.success(courseSummary2)

            useCase.invoke().test {
                val result = awaitItem()
                assertThat(result).hasSize(1)
                assertThat(result).doesNotContainKey(course1)
                assertThat(result).containsEntry(course2, courseSummary2)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `invoke - should return empty map when all summary fetches fail`() =
        runTest {
            every { courseRepository.getAllCoursesFlow() } returns flowOf(listOf(course1, course2))
            coEvery { courseSummaryRepository.get("c1") } returns Result.failure(Exception())
            coEvery { courseSummaryRepository.get("c2") } returns Result.failure(Exception())

            useCase.invoke().test {
                val result = awaitItem()
                assertThat(result).hasSize(0)
                assertThat(result).isEmpty()

                cancelAndIgnoreRemainingEvents()
            }
        }

    // ====== PERFORMANCE =======

    @Test
    fun `invoke - should handle large number of courses efficiently`() =
        runTest {
            val largeCourseList = (1..1000).map {
                CourseDto().toDomain().copy(id = "c$it")
            }
            every { courseRepository.getAllCoursesFlow() } returns flowOf(largeCourseList)
            largeCourseList.forEach {
                coEvery { courseSummaryRepository.get(it.id) } returns
                        Result.success(CourseSummaryDto().toDomain().copy(courseId = it.id))
            }

            useCase.invoke().test {
                val result = awaitItem()
                assertThat(result).hasSize(1000)
                awaitComplete()

                largeCourseList.forEach {
                    coVerify { courseSummaryRepository.get(it.id) }
                }
            }
        }
}