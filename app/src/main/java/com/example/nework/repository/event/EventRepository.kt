package com.example.nework.repository.event

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.example.nework.dto.AttachmentType
import com.example.nework.dto.Event
import com.example.nework.dto.EventItem
import com.example.nework.model.MediaModel


interface EventRepository {
    val data: Flow<PagingData<EventItem>>
    suspend fun get()
    suspend fun likeById(authToken: String, id: Int, userId: Int)
    suspend fun unlikeById(authToken: String, id: Int, userId: Int)
    suspend fun participantById(authToken: String, id: Int, userId: Int)
    suspend fun unParticipantById(authToken: String, id: Int, userId: Int)
    suspend fun removeById(authToken: String, id: Int)
    suspend fun save(authToken: String, event: Event)
    suspend fun saveWithAttachment(authToken: String, event: Event, mediaModel: MediaModel, attachmentType: AttachmentType)
}