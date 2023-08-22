package com.example.projectcopyapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseRecordDao {
    @Insert
    suspend fun insert(exerciseRecord: ExerciseRecordEntity)


    @Insert
    fun insertAll(vararg exerciseRecord: ExerciseRecordEntity)

    @Query("SELECT * FROM exercise_record_table")
    suspend fun getAllRecords(): List<ExerciseRecordEntity>

    @Query("SELECT * FROM exercise_record_table")
    fun getAll(): Flow<List<ExerciseRecordEntity>>

    @Update
    suspend fun updateExerciseRecord(record: ExerciseRecordEntity)

}