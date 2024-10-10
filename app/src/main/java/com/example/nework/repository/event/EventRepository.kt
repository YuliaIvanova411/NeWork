package com.example.nework.repository.event

import androidx.paging.PagingData
import com.example.nework.dto.Event
import com.example.nework.dto.FeedItem
import com.example.nework.model.MediaModel
import kotlinx.coroutines.flow.Flow


interface EventRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun save(event: Event)
    suspend fun saveWithAttachment(event: Event, media: MediaModel)
    suspend fun likeById(event: Event)
    suspend fun removeById(id: Int)
    suspend fun getById(id: Int): Event?
}