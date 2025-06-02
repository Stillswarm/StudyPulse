package com.studypulse.app.feat.attendance.courses.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val courseName: String,
    val courseCode: String,
    val instructor: String,
)