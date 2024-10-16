package com.example.nework.repository.post

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.example.nework.dto.AttachmentType
import com.example.nework.dto.FeedItem
import com.example.nework.dto.Post
import com.example.nework.model.MediaModel


interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
    suspend fun dataUserWall(userId: Int): Flow<PagingData<FeedItem>>
    suspend fun get()
    suspend fun getWall(userId: Int)
    suspend fun likeById(authToken: String, id: Int, userId: Int)
    suspend fun unlikeById(authToken: String, id: Int, userId: Int)
    suspend fun removeById(authToken: String, id: Int)
    suspend fun save(authToken: String, post: Post)
    suspend fun saveWithAttachment(authToken: String, post: Post, mediaModel: MediaModel, attachmentType: AttachmentType)
}