package com.example.nework.entity.wall

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nework.enumeration.RemoteKeyType


@Entity
data class WallRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int,
)