package com.example.nework.repository.user

import com.example.nework.dao.user.UserDao
import com.example.nework.error.ApiError
import com.example.nework.error.NetworkError
import com.example.nework.error.UnknownError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import com.example.nework.api.ApiService
import com.example.nework.dto.User
import com.example.nework.entity.toDto
import com.example.nework.entity.toEntity
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: ApiService,
) : UserRepository {

    override val data: Flow<List<User>> = userDao.getAll().map {
        it.toDto()
    }.flowOn(Dispatchers.Default)

    override suspend fun getAllUsers() {
        try {
            val usersResponse = apiService.getAllUsers()
            if (!usersResponse.isSuccessful) {
                throw ApiError(usersResponse.code(), usersResponse.message())
            }

            val body = usersResponse.body() ?: throw ApiError(
                usersResponse.code(),
                usersResponse.message()
            )
            userDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun getUserById(id: Int): User {
        try {
            val userResponse = apiService.getUserById(id)
            if (!userResponse.isSuccessful) {
                throw ApiError(userResponse.code(), userResponse.message())
            }
            return userResponse.body() ?: throw Exception()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun speakerById(id: Int) {
        userDao.speakerById(id)
    }

    override suspend fun unSpeakerById(id: Int) {
        userDao.unSpeakerById(id)
    }
}
