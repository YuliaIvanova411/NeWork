package com.example.nework.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nework.dao.event.EventDao
import com.example.nework.dao.event.EventRemoteKeyDao
import com.example.nework.dao.job.JobDao
import com.example.nework.dao.post.PostDao
import com.example.nework.dao.post.PostRemoteKeyDao
import com.example.nework.dao.wall.WallDao
import com.example.nework.dao.wall.WallRemoteKeyDao
import com.example.nework.entity.CoordinatesConverter
import com.example.nework.entity.ListIntConverter
import com.example.nework.entity.MapUsersPrevConverter
import com.example.nework.entity.event.EventEntity
import com.example.nework.entity.event.EventRemoteKeyEntity
import com.example.nework.entity.post.PostEntity
import com.example.nework.entity.post.PostRemoteKeyEntity
import com.example.nework.entity.job.JobEntity
import com.example.nework.entity.wall.WallEntity
import com.example.nework.entity.wall.WallRemoteKeyEntity



@Database(
    entities = [
        PostEntity::class, PostRemoteKeyEntity::class,
        WallEntity::class, WallRemoteKeyEntity::class,
        EventEntity::class, EventRemoteKeyEntity::class,
        JobEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListIntConverter::class, MapUsersPrevConverter::class, CoordinatesConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun wallDao(): WallDao
    abstract fun wallRemoteKeyDao(): WallRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun jobDao(): JobDao
}