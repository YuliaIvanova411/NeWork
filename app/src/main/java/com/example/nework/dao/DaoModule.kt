package com.example.nework.dao

import com.example.nework.dao.event.EventDao
import com.example.nework.dao.event.EventRemoteKeyDao
import com.example.nework.dao.job.JobDao
import com.example.nework.dao.post.PostDao
import com.example.nework.dao.post.PostRemoteKeyDao
import com.example.nework.dao.wall.WallDao
import com.example.nework.dao.wall.WallRemoteKeyDao
import com.example.nework.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDatabase): PostRemoteKeyDao = db.postRemoteKeyDao()

    @Provides
    fun provideWallKeyDao(db: AppDatabase): WallDao = db.wallDao()

    @Provides
    fun provideWallRemoteKeyDao(db: AppDatabase): WallRemoteKeyDao = db.wallRemoteKeyDao()

    @Provides
    fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()

    @Provides
    fun provideEventRemoteKeyDao(db: AppDatabase): EventRemoteKeyDao = db.eventRemoteKeyDao()

    @Provides
    fun provideJobDao(db: AppDatabase): JobDao = db.jobDao()
}