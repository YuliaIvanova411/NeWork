package com.example.nework.repository.user

import com.example.nework.dto.User

interface UserRepository {
    suspend fun getUserById(id: Int): User
}