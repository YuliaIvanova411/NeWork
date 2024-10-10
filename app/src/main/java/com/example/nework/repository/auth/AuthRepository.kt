package com.example.nework.repository.auth

import com.example.nework.model.AuthModel
import com.example.nework.model.MediaModel

interface AuthRepository {
    suspend fun login(login: String, password: String): AuthModel
    suspend fun register(login: String, password: String, name: String): AuthModel
    suspend fun registerWithPhoto(login: String, password: String, name: String, media: MediaModel): AuthModel
}