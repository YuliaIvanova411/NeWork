package com.example.nework.repository

import com.example.nework.repository.auth.AuthRepository
import com.example.nework.repository.auth.AuthRepositoryImpl
import com.example.nework.repository.job.JobRepository
import com.example.nework.repository.job.JobRepositoryImpl
import com.example.nework.repository.post.PostRepository
import com.example.nework.repository.post.PostRepositoryImpl
import com.example.nework.repository.event.EventRepository
import com.example.nework.repository.event.EventRepositoryImpl
import com.example.nework.repository.user.UserRepository
import com.example.nework.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository(postRepository: PostRepositoryImpl): PostRepository

    @Singleton
    @Binds
    fun bindsEventRepository(eventRepository: EventRepositoryImpl): EventRepository

    @Singleton
    @Binds
    fun bindsAuthRepository(authRepository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindsUserRepository(userRepository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    fun bindsJobRepository(jobRepository: JobRepositoryImpl): JobRepository
}