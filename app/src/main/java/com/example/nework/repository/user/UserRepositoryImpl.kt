package com.example.nework.repository.user

import com.example.nework.api.ApiService
import com.example.nework.dto.User
import com.example.nework.error.ApiError
import com.example.nework.error.NetworkError
import com.example.nework.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun getUserById(id: Int): User {
        try {
            val response = apiService.getUserById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}