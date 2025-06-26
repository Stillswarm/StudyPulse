package com.studypulse.app.feat.attendance.courses.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.studypulse.app.feat.attendance.courses.domain.model.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Insert
    suspend fun insertCourse(course: CourseEntity)

    @Query("SELECT * FROM courseentity WHERE localId = :courseId")
    suspend fun getCourseById(courseId: Long): CourseEntity

    @Query("SELECT * FROM courseentity")
    fun getAllCourses(): Flow<List<CourseEntity>>
    
    @Query("SELECT * FROM courseentity ORDER BY courseName ASC")
    fun getAllCoursesSortedByName(): Flow<List<CourseEntity>>
}