package com.example.nework.dao.post

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nework.entity.post.PostRemoteKeyEntity

@Dao
interface PostRemoteKeyDao {

    @Query("SELECT MAX(id) FROM PostRemoteKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT MIN(id) FROM PostRemoteKeyEntity")
    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: PostRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoteKeyEntity: List<PostRemoteKeyEntity>)

    @Query("DELETE FROM PostRemoteKeyEntity")
    suspend fun removeAll()
}