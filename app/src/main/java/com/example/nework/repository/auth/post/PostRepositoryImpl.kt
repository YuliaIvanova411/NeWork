package com.example.nework.repository.auth.post

import androidx.paging.*
import com.example.nework.api.ApiService
import com.example.nework.dto.Attachment
import com.example.nework.dto.FeedItem
import com.example.nework.dto.Media
import com.example.nework.dto.Post
import com.example.nework.error.NetworkError
import com.example.nework.model.MediaModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.example.nework.dao.post.PostDao
import com.example.nework.dao.post.PostRemoteKeyDao
import com.example.nework.dao.wall.WallDao
import com.example.nework.dao.wall.WallRemoteKeyDao
import com.example.nework.db.AppDatabase
import com.example.nework.entity.post.PostEntity
import com.example.nework.entity.wall.WallEntity
import com.example.nework.error.ApiError
import com.example.nework.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDatabase,
    private val wallDao: WallDao,
    private val wallRemoteKeyDao: WallRemoteKeyDao
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(
            service = apiService,
            appDb = appDb,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao
        ),
        pagingSourceFactory = postDao::getPagingSource,
    ).flow
        .map { it.map(PostEntity::toDto) }

    @OptIn(ExperimentalPagingApi::class)
    override fun userWall(id: Int): Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            service = apiService,
            appDb = appDb,
            wallDao = wallDao,
            wallRemoteKeyDao = wallRemoteKeyDao,
            authorId = id
        ),
        pagingSourceFactory = wallDao::getPagingSource,
    ).flow
        .map { it.map(WallEntity::toDto) }

    override suspend fun likeById(post: Post) {
        try {
            val response =
                if (!post.likedByMe) {
                    apiService.likePostById(post.id)
                } else {
                    apiService.dislikePostById(post.id)
                }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.createPost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, media: MediaModel) {
        try {
            val uploadMedia = upload(media)

            val response = apiService.createPost(
                post.copy(
                    attachment = Attachment(uploadMedia.url, media.type),
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(media: MediaModel): Media {
        try {
            val part = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )
            val response = apiService.uploadMedia(part)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        postDao.getPostById(id)?.let { postToDelete ->
            postDao.removeById(id)
            try {
                val response = apiService.deletePost(id)
                if (!response.isSuccessful) {
                    postDao.insert(postToDelete)
                    throw ApiError(response.code(), response.message())
                }
            } catch (e: IOException) {
                postDao.insert(postToDelete)
                throw NetworkError
            } catch (e: Exception) {
                postDao.insert(postToDelete)
                throw UnknownError
            }
        }
    }

    override suspend fun getById(id: Int): Post? =
        postDao.getPostById(id)?.toDto()

    override suspend fun wallRemoveById(id: Int) = wallDao.removeById(id)
}