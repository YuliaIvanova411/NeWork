package com.example.nework.repository.job

import com.example.nework.dao.job.JobDao
import com.example.nework.entity.job.JobEntity
import com.example.nework.entity.job.toDto
import com.example.nework.entity.job.toEntity
import com.example.nework.error.ApiError
import com.example.nework.error.NetworkError
import com.example.nework.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import com.example.nework.api.ApiService
import com.example.nework.dto.Job
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao,
    private val apiService: ApiService,
) : JobRepository {

    override val data: Flow<List<Job>> = jobDao.getAll().map {
        it.toDto()
    }.flowOn(Dispatchers.Default)

    override suspend fun getJobsById(userId: Int) {
        try {
            jobDao.removeAll()
            val jobResponse = apiService.getJobsById(userId)
            if (!jobResponse.isSuccessful) {
                throw ApiError(jobResponse.code(), jobResponse.message())
            }
            val body = jobResponse.body() ?: throw ApiError(
                jobResponse.code(),
                jobResponse.message()
            )
            jobDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun saveJob(authToken: String, job: Job) {
        try {
            val jobResponse = apiService.saveJob(authToken, job)
            if (!jobResponse.isSuccessful) {
                throw ApiError(jobResponse.code(), jobResponse.message())
            }

            val body = jobResponse.body() ?: throw ApiError(
                jobResponse.code(),
                jobResponse.message()
            )
            jobDao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun removeJob(authToken: String, id: Int) {
        try {
            val jobResponse = apiService.removeJob(authToken, id)
            if (!jobResponse.isSuccessful) {
                throw ApiError(jobResponse.code(), jobResponse.message())
            }
            jobDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }
}