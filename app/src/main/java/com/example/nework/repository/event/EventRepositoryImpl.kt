package com.example.nework.repository.event

import androidx.paging.*
import com.example.nework.api.ApiService
import com.example.nework.dao.event.EventDao
import com.example.nework.dao.event.EventRemoteKeyDao
import com.example.nework.db.AppDatabase
import com.example.nework.dto.Attachment
import com.example.nework.dto.Event
import com.example.nework.dto.FeedItem
import com.example.nework.dto.Media
import com.example.nework.entity.event.EventEntity
import com.example.nework.error.ApiError
import com.example.nework.error.NetworkError
import com.example.nework.model.MediaModel
import com.example.nework.error.UnknownError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
import javax.inject.Inject


class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDatabase
) : EventRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = EventRemoteMediator(
            service = apiService,
            appDb = appDb,
            eventDao = eventDao,
            eventRemoteKeyDao = eventRemoteKeyDao
        ),
        pagingSourceFactory = eventDao::getPagingSource
    ).flow.map { it.map(EventEntity::toDto) }

    override suspend fun save(event: Event) {
        try {
            val response = apiService.createEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun saveWithAttachment(event: Event, media: MediaModel) {
        try {
            val uploadMedia = upload(media)
            val response = apiService.createEvent(
                event.copy(
                    attachment = Attachment(uploadMedia.url, media.type)
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun upload(media: MediaModel): Media {
        val part = MultipartBody.Part.createFormData(
            "file", media.file.name, media.file.asRequestBody()
        )
        val response = apiService.uploadMedia(part)
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return requireNotNull(response.body())
    }

    override suspend fun likeById(event: Event) {
        try {
            val response =
                if (!event.likedByMe) {
                    apiService.likeEventById(event.id)
                } else {
                    apiService.dislikeEventById(event.id)
                }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        eventDao.getEventById(id)?.let { eventToDelete ->
            eventDao.removeById(id)
            try {
                val response = apiService.deleteEvent(id)
                if (!response.isSuccessful) {
                    eventDao.insert(eventToDelete)
                    throw ApiError(response.code(), response.message())
                }
            } catch (e: IOException) {
                eventDao.insert(eventToDelete)
                throw NetworkError
            } catch (e: Exception) {
                eventDao.insert(eventToDelete)
                throw UnknownError
            }
        }
    }

    override suspend fun getById(id: Int): Event? =
        eventDao.getEventById(id)?.toDto()

}