package com.example.nework.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import com.example.nework.auth.AppAuth
import com.example.nework.enumeration.AttachmentType
import com.example.nework.model.AuthModel
import com.example.nework.model.AuthModelState
import com.example.nework.model.MediaModel
import com.example.nework.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthModel> = appAuth
        .authState
        .asLiveData(Dispatchers.Default)

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    private val _media = MutableLiveData<MediaModel?>(null)
    val media: LiveData<MediaModel?>
        get() = _media


    fun register(login: String, pass: String, name: String) = viewModelScope.launch {
        if (login.isNotBlank() && pass.isNotBlank() && name.isNotBlank()) {
            val avatar = media.value
            if (avatar != null) {
                try {
                    _state.value = AuthModelState(loading = true)
                    val result = repository.registerWithPhoto(login, pass, name, avatar)
                    appAuth.setAuth(result.id, result.token)
                    _state.value = AuthModelState(loggedIn = true)
                } catch (e: Exception) {
                    _state.value = AuthModelState(error = true)
                }

            } else {
                try {
                    _state.value = AuthModelState(loading = true)
                    val result = repository.register(login, pass, name)
                    appAuth.setAuth(result.id, result.token)
                    _state.value = AuthModelState(loggedIn = true)
                } catch (e: Exception) {
                    _state.value = AuthModelState(error = true)
                }
            }
        } else {
            _state.value = AuthModelState(isBlank = true)
        }
        _state.value = AuthModelState()
        clearPhoto()
    }

    fun addPhoto(uri: Uri, file: File, type: AttachmentType) {
        _media.value = MediaModel(uri, file, type)
    }

    fun clearPhoto() {
        _media.value = null
    }
}