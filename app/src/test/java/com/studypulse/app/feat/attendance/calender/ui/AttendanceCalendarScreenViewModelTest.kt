package com.studypulse.app.feat.attendance.calender.ui

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.studypulse.app.SnackbarController
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.fixedDate
import com.studypulse.app.mockAttendance
import com.studypulse.app.mockPeriod
import com.studypulse.app.sem1
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceCalendarScreenViewModelTest {
    private lateinit var vm: AttendanceCalendarScreenViewModel

    private val initialData = AttendanceCalendarScreenState().copy(
        semesterId = "sem1",
        semesterStartDate = LocalDate.parse("2025-01-01"),
        semesterEndDate = LocalDate.parse("2025-12-31"),
        yearMonth = YearMonth.of(2025, 1),
        periodsList = listOf(
            PeriodWithAttendance(
                mockk(),
                mockk()
            ),
            PeriodWithAttendance(
                mockk(),
                mockk()
            )
        )
    )

    fun initVm(
        periodRepository: PeriodRepository = mockk() {
            coEvery {
                getAllPeriodsFilteredByDayOfWeek(any())
            } returns Result.success(flow { emit(listOf(mockPeriod)) })
        }
    ) {
        vm = AttendanceCalendarScreenViewModel(
            attendanceRepository = mockk {
                coEvery {
                    getDatesWithUnmarkedAttendance(any(), any(), any())
                } returns Result.success(setOf(fixedDate))

                coEvery {
                    getAttendanceForPeriodAndDate(any(), any())
                } returns mockAttendance
            },
            semesterRepository = mockk {
                coEvery { getActiveSemester() } returns Result.success(sem1)
            },
            periodRepository = periodRepository,
            initialData
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        initVm()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onDateSelected updates periodsList to empty if date is outside semester range`() =
        runTest {
            vm.state.test {
                awaitItem()
                vm.onDateSelected(LocalDate.parse("2024-01-01"))
                assertThat(awaitItem().periodsList).isEmpty()
            }
        }

    @Test
    fun `onDateSelected updates periodsList correctly (for valid date)`() =
        runTest {
            initVm()
            vm.state.test {
                awaitItem()
                awaitItem()
                vm.onDateSelected(LocalDate.parse("2025-01-04"))
                advanceUntilIdle()
                val periodsList = awaitItem().periodsList
                assertThat(periodsList).isNotEmpty()
                val pwa = periodsList.first()
                assertThat(pwa.period).isEqualTo(mockPeriod)
                assertThat(pwa.attendanceRecord).isEqualTo(mockAttendance)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onDateSelected shows snackbar when schedule cannot be fetched`() =
        runTest {
            mockkObject(SnackbarController)
            coEvery { SnackbarController.sendEvent(any()) } returns Unit

            initVm(
                periodRepository = mockk {
                    coEvery { getAllPeriodsFilteredByDayOfWeek(any()) } returns
                            Result.failure(Exception())
                }
            )


            vm.state.test {
                awaitItem()
                awaitItem()
                vm.onDateSelected(fixedDate)
                advanceUntilIdle()

                coVerify { SnackbarController.sendEvent(match { it.message.contentEquals("couldn't fetch schedule") }) }
            }
        }

    @Test
    fun `onMonthChanged updates yearMonth and unmarkedDates`() =
        runTest {
            initVm()

            val newYearMonth = YearMonth.of(2025, 5)
            vm.state.test {
                awaitItem()
                awaitItem()
                vm.onMonthChanged(newYearMonth)
                advanceUntilIdle()
                val state = awaitItem()
                assertThat(state.unmarkedDates).containsExactly(fixedDate)
                assertThat(state.yearMonth).isEqualTo(newYearMonth)

                cancelAndIgnoreRemainingEvents()
            }
        }
}