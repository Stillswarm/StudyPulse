package com.studypulse.app.feat.attendance.di

import com.studypulse.app.StudyPulseDatabase
import com.studypulse.app.feat.attendance.attendance.data.RoomAttendanceRepositoryImpl
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceDao
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceRepository
import com.studypulse.app.feat.attendance.courses.data.RoomCourseRepositoryImpl
import com.studypulse.app.feat.attendance.courses.data.RoomPeriodRepositoryImpl
import com.studypulse.app.feat.attendance.courses.domain.CourseDao
import com.studypulse.app.feat.attendance.courses.domain.CourseRepository
import com.studypulse.app.feat.attendance.courses.domain.PeriodDao
import com.studypulse.app.feat.attendance.courses.domain.PeriodRepository
import com.studypulse.app.feat.attendance.courses.presentation.course.CoursesScreenViewModel
import com.studypulse.app.feat.attendance.courses.presentation.add_course.AddCourseViewModel
import com.studypulse.app.feat.attendance.courses.presentation.course_details.CourseDetailsScreenViewModel
import com.studypulse.app.feat.attendance.courses.presentation.add_period.AddPeriodScreenViewModel
import com.studypulse.app.feat.attendance.attendance.presentation.home.AttendanceScreenViewModel
import com.studypulse.app.feat.attendance.calender.ui.AttendanceCalendarScreenViewModel
import com.studypulse.app.feat.attendance.attendance.presentation.AttendanceStatsSharedViewModel
import com.studypulse.app.feat.attendance.schedule.presentation.ScheduleScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val attendanceModule = module {
    // DAO
    single<CourseDao> { get<StudyPulseDatabase>().courseDao() }
    single<PeriodDao> { get<StudyPulseDatabase>().periodDao() }
    single<AttendanceDao> { get<StudyPulseDatabase>().attendanceDao() }

    // repository
    single<CourseRepository> { RoomCourseRepositoryImpl(get()) }
    single<PeriodRepository> { RoomPeriodRepositoryImpl(get()) }
    single<AttendanceRepository> { RoomAttendanceRepositoryImpl(get()) }

    // VM
    viewModelOf(::CoursesScreenViewModel)
    viewModelOf(::ScheduleScreenViewModel)
    viewModelOf(::AddCourseViewModel)
    viewModelOf(::CourseDetailsScreenViewModel)
    viewModelOf(::AddPeriodScreenViewModel)
    viewModelOf(::AttendanceScreenViewModel)
    viewModelOf(::AttendanceCalendarScreenViewModel)
    viewModelOf(::AttendanceStatsSharedViewModel)

}