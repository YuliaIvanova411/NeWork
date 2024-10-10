package com.example.nework.repository.auth

import com.example.nework.api.ApiService
import com.example.nework.model.AuthModel
import com.example.nework.model.MediaModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.nework.error.ApiError
import com.example.nework.error.NetworkError
import com.example.nework.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(login: String, password: String): AuthModel {
        try {
            val response = apiService.login(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body : AuthModel = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)
        } catch (e: ApiError) {
            throw ApiError(e.status, e.code)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun register(login: String, password: String, name: String): AuthModel {
        try {
            val response = apiService.register(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body : AuthModel = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerWithPhoto(
        login: String,
        password: String,
        name: String,
        media: MediaModel
    ): AuthModel {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )
            val loginRequestBody = login.toRequestBody("text/plain".toMediaType())
            val passRequestBody = password.toRequestBody("text/plain".toMediaType())
            val nameRequestBody = login.toRequestBody("text/plain".toMediaType())
            val response = apiService.registerWithPhoto(
                loginRequestBody,
                passRequestBody,
                nameRequestBody,
                part
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body : AuthModel = response.body() ?: throw ApiError(response.code(), response.message())
            return AuthModel(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}