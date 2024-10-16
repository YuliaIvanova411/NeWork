package com.example.nework.repository.post

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.nework.error.ApiError
import com.example.nework.error.NetworkError
import com.example.nework.error.UnknownError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import com.example.nework.api.ApiService
import com.example.nework.dao.post.PostDao
import com.example.nework.dao.post.PostRemoteKeyDao
import com.example.nework.db.AppDb
import com.example.nework.dto.Attachment
import com.example.nework.dto.AttachmentType
import com.example.nework.dto.FeedItem
import com.example.nework.dto.Media
import com.example.nework.dto.Post
import com.example.nework.entity.post.PostEntity
import com.example.nework.entity.post.toEntity
import com.example.nework.model.MediaModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.allPostPaging() },
        remoteMediator = PostRemoteMediator(apiService, postDao, postRemoteKeyDao, appDb),
    ).flow
        .map { it.map(PostEntity::toDto) }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun dataUserWall(userId: Int): Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getUserWallPaging(userId) },
        remoteMediator = PostUserWallMediator(apiService, postDao, postRemoteKeyDao, appDb, userId),
    ).flow
        .map { it.map(PostEntity::toDto) }

    override suspend fun get() {
        try {
            val postsResponse = apiService.getAll()
            if (!postsResponse.isSuccessful) {
                throw ApiError(postsResponse.code(), postsResponse.message())
            }

            val body = postsResponse.body() ?: throw ApiError(
                postsResponse.code(),
                postsResponse.message()
            )
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun getWall(userId: Int) {
        try {
            val postsResponse = apiService.getUserWall(userId)
            if (!postsResponse.isSuccessful) {
                throw ApiError(postsResponse.code(), postsResponse.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun save(authToken: String, post: Post) {
        try {
            val postsResponse = apiService.savePost(authToken, post)
            if (!postsResponse.isSuccessful) {
                throw ApiError(postsResponse.code(), postsResponse.message())
            }

            val body = postsResponse.body() ?: throw ApiError(
                postsResponse.code(),
                postsResponse.message()
            )
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(
        authToken: String,
        post: Post,
        mediaModel: MediaModel,
        attachmentType: AttachmentType
    ) {
        try {
            val media = upload(authToken, mediaModel)
            val postsResponse = apiService.savePost(
                authToken,
                post.copy(
                    attachment = Attachment(media.url, attachmentType)
                )
            )

            if (!postsResponse.isSuccessful) {
                throw ApiError(postsResponse.code(), postsResponse.message())
            }

            val body = postsResponse.body() ?: throw ApiError(
                postsResponse.code(),
                postsResponse.message()
            )
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    private suspend fun upload(authToken: String, media: MediaModel): Media {
        val part = MultipartBody.Part.createFormData(
            "file", media.file.name, media.file.asRequestBody()
        )

        val postsResponse = apiService.uploadMedia(authToken, part)
        if (!postsResponse.isSuccessful) {
            throw ApiError(postsResponse.code(), postsResponse.message())
        }

        return requireNotNull(postsResponse.body())
    }

    override suspend fun likeById(authToken: String, id: Int, userId: Int) {
        try {
            val postsResponse = apiService.likePostById(authToken, id)
            if (!postsResponse.isSuccessful) {
                throw ApiError(postsResponse.code(), postsResponse.message())
            }
            postDao.likedById(id, userId)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun unlikeById(authToken: String, id: Int, userId: Int) {
        try {
            val postsResponse = apiService.unlikePostById(authToken, id)
            if (!postsResponse.isSuccessful) {
                throw ApiError(postsResponse.code(), postsResponse.message())
            }
            postDao.unlikedById(id, userId)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }

    override suspend fun removeById(authToken: String, id: Int) {
        try {
            val postsResponse = apiService.removePostById(authToken, id)
            if (!postsResponse.isSuccessful) {
                throw ApiError(postsResponse.code(), postsResponse.message())
            }
            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
            throw UnknownError
        }
    }
}