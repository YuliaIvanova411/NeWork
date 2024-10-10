package com.example.nework.repository.auth.job

import com.example.nework.api.ApiService
import com.example.nework.entity.job.JobEntity
import com.example.nework.entity.job.toDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import com.example.nework.dao.job.JobDao
import com.example.nework.dto.Job
import com.example.nework.entity.job.toEntity
import com.example.nework.error.ApiError
import com.example.nework.error.NetworkError
import com.example.nework.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService
) : JobRepository {

    override val data = jobDao.getAll()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getJobsById(id: Int) {
        try {
            jobDao.removeAll()
            val response = apiService.getJobsByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val data = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(data.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(job: Job) {
        try {
            val response = apiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val data = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(data))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            jobDao.removeById(id)
            val response = apiService.removeJobById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}