package com.example.nework.repository.auth.job

import com.example.nework.dto.Job
import kotlinx.coroutines.flow.Flow


interface JobRepository {
    val data: Flow<List<Job>>
    suspend fun getJobsById(id: Int)
    suspend fun save(job: com.example.nework.dto.Job)
    suspend fun removeById(id: Int)
}