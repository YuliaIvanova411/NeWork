package com.example.nework.dao.wall

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nework.entity.wall.WallRemoteKeyEntity

@Dao
interface WallRemoteKeyDao {

    @Query("SELECT max(`id`) FROM WallRemoteKeyEntity")
    suspend fun max(): Int?

    @Query("SELECT min(`id`) FROM WallRemoteKeyEntity")
    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: WallRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<WallRemoteKeyEntity>)

    @Query("DELETE FROM WallRemoteKeyEntity")
    suspend fun removeAll()
}