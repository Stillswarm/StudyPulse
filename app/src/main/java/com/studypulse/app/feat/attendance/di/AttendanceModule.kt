package com.studypulse.app.feat.attendance.di

import com.studypulse.app.StudyPulseDatabase
import com.studypulse.app.feat.attendance.attendance.data.FirebaseAttendanceRepositoryImpl
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceDao
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.attendance.domain.use_cases.GetCourseWiseSummariesUseCase
import com.studypulse.app.feat.attendance.attendance.domain.use_cases.GetCourseWiseSummariesUseCaseImpl
import com.studypulse.app.feat.attendance.attendance.presentation.AttendanceStatsSharedViewModel
import com.studypulse.app.feat.attendance.attendance.presentation.home.AttendanceScreenState
import com.studypulse.app.feat.attendance.attendance.presentation.home.AttendanceScreenViewModel
import com.studypulse.app.feat.attendance.attendance.presentation.overview.AttendanceOverviewScreenViewModel
import com.studypulse.app.feat.attendance.calender.ui.AttendanceCalendarScreenState
import com.studypulse.app.feat.attendance.calender.ui.AttendanceCalendarScreenViewModel
import com.studypulse.app.feat.attendance.courses.data.FirebaseCourseRepositoryImpl
import com.studypulse.app.feat.attendance.courses.data.FirebaseCourseSummaryRepositoryImpl
import com.studypulse.app.feat.attendance.courses.data.FirebasePeriodRepositoryImpl
import com.studypulse.app.feat.attendance.courses.domain.CourseDao
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.CourseSummaryRepository
import com.studypulse.app.feat.attendance.courses.domain.PeriodDao
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.presentation.add_course.AddCourseScreenViewModel
import com.studypulse.app.feat.attendance.courses.presentation.add_period.AddPeriodScreenViewModel
import com.studypulse.app.feat.attendance.courses.presentation.course.CoursesScreenViewModel
import com.studypulse.app.feat.attendance.courses.presentation.course_details.CourseDetailsScreenViewModel
import com.studypulse.app.feat.attendance.schedule.presentation.ScheduleScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val attendanceModule = module {
    // DAO
    single<CourseDao> { get<StudyPulseDatabase>().courseDao() }
    single<PeriodDao> { get<StudyPulseDatabase>().periodDao() }
    single<AttendanceDao> { get<StudyPulseDatabase>().attendanceDao() }

    // room repository
//    single<CourseRepository> { RoomCourseRepositoryImpl(get()) }
//    single<PeriodRepository> { RoomPeriodRepositoryImpl(get()) }
//    single<AttendanceRepository> { RoomAttendanceRepositoryImpl(get()) }

    // firebase repository
    single<CourseRepository> { FirebaseCourseRepositoryImpl(get(), get(), get(),get(),get()) }
    single<CourseSummaryRepository> { FirebaseCourseSummaryRepositoryImpl( get(), get(), get(), get()) }
    single<PeriodRepository> { FirebasePeriodRepositoryImpl(get(), get(), get(), get(), get(), get(), get()) }
    single<AttendanceRepository> { FirebaseAttendanceRepositoryImpl(get(), get(), get(), get()) }

    // state holding classes
    single { AttendanceScreenState() }
    single { AttendanceCalendarScreenState() }

    // use-cases
    factoryOf(::GetCourseWiseSummariesUseCaseImpl) bind GetCourseWiseSummariesUseCase::class

    // VM
    viewModelOf(::CoursesScreenViewModel)
    viewModelOf(::ScheduleScreenViewModel)
    viewModelOf(::AddCourseScreenViewModel)
    viewModelOf(::CourseDetailsScreenViewModel)
    viewModelOf(::AddPeriodScreenViewModel)
    viewModelOf(::AttendanceScreenViewModel)
    viewModelOf(::AttendanceCalendarScreenViewModel)
    viewModelOf(::AttendanceStatsSharedViewModel)
    viewModelOf(::AttendanceOverviewScreenViewModel)

}