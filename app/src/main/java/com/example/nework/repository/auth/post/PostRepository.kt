package com.example.nework.repository.auth.post

import androidx.paging.PagingData
import com.example.nework.dto.FeedItem
import com.example.nework.dto.Post
import com.example.nework.model.MediaModel
import kotlinx.coroutines.flow.Flow

interface PostRepository  {
    val data: Flow<PagingData<FeedItem>>
    fun userWall(id: Int): Flow<PagingData<FeedItem>>
    suspend fun likeById(post: Post)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, media: MediaModel)
    suspend fun removeById(id: Int)
    suspend fun getById(id: Int): Post?
    suspend fun wallRemoveById(id: Int)
}