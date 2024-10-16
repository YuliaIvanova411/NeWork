package com.example.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nework.dao.job.JobDao
import com.example.nework.dao.user.UserDao
import com.example.nework.entity.Converter
import com.example.nework.entity.job.JobEntity
import com.example.nework.dao.event.EventDao
import com.example.nework.dao.event.EventRemoteKeyDao
import com.example.nework.dao.post.PostDao
import com.example.nework.dao.post.PostRemoteKeyDao
import com.example.nework.entity.event.EventEntity
import com.example.nework.entity.event.EventRemoteKeyEntity
import com.example.nework.entity.post.PostEntity
import com.example.nework.entity.post.PostRemoteKeyEntity
import com.example.nework.entity.UserEntity

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        UserEntity::class,
        JobEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
}