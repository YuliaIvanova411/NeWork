package com.example.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.nework.dto.Coordinates
import com.example.nework.dto.Event
import com.example.nework.dto.FeedItem
import com.example.nework.enumeration.AttachmentType
import com.example.nework.enumeration.EventType
import com.example.nework.model.FeedModelState
import com.example.nework.model.MediaModel
import com.example.nework.repository.event.EventRepository
import com.example.nework.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    published = "",
    coords = null,
    link = null,
    users = emptyMap(),
    datetime = "",
    type = EventType.ONLINE,
    participatedByMe = false
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = cached

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Event>
    get() = _edited

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    fun addMedia(uri: Uri, file: File, type: AttachmentType) {
        _media.value = MediaModel(uri, file, type)
    }

    fun clearMedia() {
        _media.value = null
        _edited.value = _edited.value?.copy(attachment = null)
    }

    fun save() {
        edited.value?.let {
            if (it !== empty) {
                viewModelScope.launch {
                    try {
                        when (val media = media.value) {
                            null -> repository.save(it)
                            else -> {
                                repository.saveWithAttachment(it, media)
                            }
                        }
                        _eventCreated.value = Unit
                        clearEdited()
                        clearMedia()
                        _dataState.value = FeedModelState()
                    } catch (e: Exception) {
                        _dataState.value = FeedModelState(error = true)
                    }
                }
            }
        }
    }

    fun edit(event: Event) {
        _edited.value = event
    }
    fun clearEdited() {
        _edited.value = empty
    }

    fun getEditEvent(): Event? {
        return if (edited.value == null || edited.value == empty) null else edited.value
    }

    fun addCoordinates(coords: Coordinates) {
        viewModelScope.launch {
            _edited.value = _edited.value?.copy(coords = coords)
        }
    }

    fun clearCoordinates() {
        viewModelScope.launch {
            _edited.value = _edited.value?.copy(coords = null)
        }
    }

    fun changeContent(
        content: String,
        datetime: String,
        eventType: EventType,
        link: String?,
    ) {
        val text = content.trim()
        _edited.value =
            edited.value?.copy(content = text, datetime = datetime, type = eventType, link = link)
    }

    fun likeById(event: Event) {
        viewModelScope.launch {
            try {
                repository.likeById(event)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }

        }
    }

    fun removeById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }
}