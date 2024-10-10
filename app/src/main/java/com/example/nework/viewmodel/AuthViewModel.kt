package com.example.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nework.model.AuthModel
import com.example.nework.model.AuthModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.nework.auth.AppAuth
import com.example.nework.error.ApiError
import com.example.nework.repository.auth.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    val data: LiveData<AuthModel> = appAuth
        .authState
        .asLiveData(Dispatchers.Default)

    val authorized: Boolean
        get() = data.value != AuthModel()

    private val _state = MutableLiveData<AuthModelState>()
    val state: LiveData<AuthModelState>
        get() = _state

    fun login(login: String, password: String) = viewModelScope.launch {
        if (login.isNotBlank() && password.isNotBlank()) {
            try {
                _state.value = AuthModelState(loading = true)
                val result = repository.login(login, password)
                appAuth.setAuth(result.id, result.token)
                _state.value = AuthModelState(loggedIn = true)
            } catch (e: Exception) {
                when (e) {
                    is ApiError -> if (e.status == 404) _state.value =
                        AuthModelState(invalidLoginOrPass = true)
                    else -> _state.value = AuthModelState(error = true)
                }
            }
        } else {
            _state.value = AuthModelState(isBlank = true)
        }
        _state.value = AuthModelState()
    }

    fun logout() {
        appAuth.removeAuth()
        _state.value = AuthModelState(notLoggedIn = true)
    }
}