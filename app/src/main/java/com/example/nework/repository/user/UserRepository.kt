package com.example.nework.repository.user

import kotlinx.coroutines.flow.Flow
import com.example.nework.dto.User

interface UserRepository {
    val data: Flow<List<User>>
    suspend fun getAllUsers()
    suspend fun getUserById(id: Int) : User
    suspend fun speakerById(id: Int)
    suspend fun unSpeakerById(id: Int)
}