package com.studypulse.app.feat.attendance.attendance.presentation.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.studypulse.app.SnackbarController
import com.studypulse.app.common.datastore.AppDatastore
import com.studypulse.app.feat.attendance.courses.domain.CourseSummary
import com.studypulse.app.feat.semester.domain.SemesterRepository
import com.studypulse.app.feat.semester.domain.model.SemesterSummary
import com.studypulse.app.sem1
import com.studypulse.app.sem2
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceScreenViewModelTest {

    private val dummySemesters = listOf(sem1, sem2)

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var ds: AppDatastore
    private lateinit var semesterRepo: SemesterRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        ds = mockk {
            every { semesterIdFlow } returns flowOf("sem1")
        }
        semesterRepo = mockk<SemesterRepository> {
            coEvery { getAllSemesters() } returns Result.success(dummySemesters)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init block toggles loading and fetches data in order`() = testScope.runTest {
        val ds = mockk<AppDatastore> { every { semesterIdFlow } returns flowOf("sem1") }

        val vm = AttendanceScreenViewModel(
            mockk(),
            mockk(),
            semesterRepo,
            ds,
        )

        vm.state.test {
            assertThat(awaitItem().isLoading).isFalse()
            assertThat(awaitItem().isLoading).isTrue()

            val afterSemFetch = awaitItem()
            assertThat(afterSemFetch.semesterList).containsExactlyElementsIn(dummySemesters)
                .inOrder()

            assertThat(awaitItem().isLoading).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getPercent returns correct percent`() {
        val vm = AttendanceScreenViewModel(mockk(), mockk(), mockk(), ds)
        val course = mockk<CourseSummary> {
            every { presentRecords } returns 15
            every { absentRecords } returns 5
            every { unmarkedRecords } returns 0
            every { minAttendance } returns 75
        }
        assertThat(vm.getPercent(course)).isEqualTo(75)

        val zeroCourse = mockk<CourseSummary> {
            every { presentRecords } returns 0
            every { absentRecords } returns 0
            every { unmarkedRecords } returns 0
            every { minAttendance } returns 75
        }
        assertThat(vm.getPercent(zeroCourse)).isEqualTo(0)
    }

    @Test
    fun `getPercentForSem returns correct percent`() {
        val vm = AttendanceScreenViewModel(
            mockk(), mockk(), mockk(), ds
        )
        val summary = mockk<SemesterSummary> {
            every { presentRecords } returns 20
            every { absentRecords } returns 5
            every { unmarkedRecords } returns 5
        }
        assertThat(vm.getPercentForSem(summary)).isEqualTo(66)

        val zeroSummary = mockk<SemesterSummary> {
            every { presentRecords } returns 0
            every { absentRecords } returns 0
            every { unmarkedRecords } returns 0
        }
        assertThat(vm.getPercentForSem(zeroSummary)).isEqualTo(0)
    }

    @Test
    fun `onChangeActiveSemester does not change to past semester and shows snackbar`() = runTest {
        val oldSemester = sem1.copy(endDate = LocalDate.now().minusDays(1))
        val currentState = AttendanceScreenState(activeSemester = sem2)

        mockkObject(SnackbarController)
        coEvery { SnackbarController.sendEvent(any()) } returns Unit
        val vm = AttendanceScreenViewModel(
            mockk(), mockk(), semesterRepo, ds,currentState
        )

        vm.onChangeActiveSemester(oldSemester)

        advanceUntilIdle()

        // Should not update to past semester
        assertThat(vm.state.value.activeSemester).isNotEqualTo(oldSemester)
        coVerify { SnackbarController.sendEvent(match { it.message.contains("Active semester cannot be in the past") }) }
    }
}