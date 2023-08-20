package com.example.projectcopyapp.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_record_table")
data class ExerciseRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("month") val month: Int? = null,
    @ColumnInfo("day") val day: Int? = null,
    @ColumnInfo("startTime") val startTime: String? = null,
    @ColumnInfo("endTime") val endTime: String? = null,
    @ColumnInfo("exerciseRecord") val exerciseRecord: String? = null,
    @ColumnInfo("selectedImageUri") val selectedImageUri: Uri? = null,
    @ColumnInfo("emotion") val emotion: String? = null
    )
