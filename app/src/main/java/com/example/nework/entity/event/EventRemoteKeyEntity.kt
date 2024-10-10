package com.example.nework.entity.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nework.enumeration.RemoteKeyType

@Entity
data class EventRemoteKeyEntity(
    @PrimaryKey
    val type: RemoteKeyType,
    val id: Int
)