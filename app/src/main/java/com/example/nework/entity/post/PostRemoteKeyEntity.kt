package com.example.nework.entity.post

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nework.enumeration.RemoteKeyType

@Entity
data class PostRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int,
)