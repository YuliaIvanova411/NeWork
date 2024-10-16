package com.example.nework.dao.job

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nework.entity.job.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Query("SELECT * FROM JobEntity ORDER BY id DESC")
    fun getAll(): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: List<JobEntity>)

    @Query("SELECT * FROM JobEntity WHERE id = :id")
    suspend fun getJob(id: Int): JobEntity

    @Query("DELETE FROM JobEntity")
    suspend fun removeAll()

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Int)
}
