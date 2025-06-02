package com.studypulse.app.feat.attendance.courses.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studypulse.app.feat.attendance.courses.data.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Insert
    suspend fun insertCourse(course: Course)

    @Query("SELECT * FROM course WHERE id = :courseId")
    suspend fun getCourseById(courseId: Long): Course

    @Query("SELECT * FROM course")
    fun getAllCourses(): Flow<List<Course>>
    
    @Query("SELECT * FROM course ORDER BY courseName ASC")
    fun getAllCoursesSortedByName(): Flow<List<Course>>
}