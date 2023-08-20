package com.example.projectcopyapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.projectcopyapp.UriTypeConverter

@Database(entities = [ExerciseRecordEntity::class], version = 3)
@TypeConverters(UriTypeConverter::class)
abstract class ExerciseDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseRecordDao

    companion object {
        @Volatile
        private var INSTANCE : ExerciseDatabase? = null

        fun getDatabase(context: Context): ExerciseDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    ExerciseDatabase::class.java, "exercise_database"
                ).build()
                db
            }
        }
    }
}