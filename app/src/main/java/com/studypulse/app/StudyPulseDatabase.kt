package com.studypulse.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.studypulse.app.feat.attendance.attendance.domain.AttendanceDao
import com.studypulse.app.feat.attendance.attendance.domain.model.AttendanceRecordEntity
import com.studypulse.app.feat.attendance.courses.domain.CourseDao
import com.studypulse.app.feat.attendance.courses.domain.PeriodDao
import com.studypulse.app.feat.attendance.courses.domain.model.CourseEntity
import com.studypulse.app.feat.attendance.courses.domain.model.PeriodEntity
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
}

/**
 * Initialize database, provide entities, and declare DAOs
 */
@Database(entities = [CourseEntity::class, PeriodEntity::class, AttendanceRecordEntity::class], version = 9, exportSchema = false)
@TypeConverters(Converters::class)
abstract class StudyPulseDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun periodDao(): PeriodDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        // for efficiency, database is created only once
        // once created, the database instance is stored in
        // the Instance variable. This value is returned
        // for all subsequent fetches
        private var Instance: StudyPulseDatabase? = null

        fun getDatabase(context: Context): StudyPulseDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    name = "student_database",
                    klass = StudyPulseDatabase::class.java
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
