package com.example.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nework.dto.User
import com.example.nework.model.FeedModelState
import com.example.nework.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userDataState = MutableLiveData<FeedModelState>()
    val userDataState: LiveData<FeedModelState>
        get() = _userDataState

    fun getUserById(id: Int) = viewModelScope.launch {
        try {
            _user.value = userRepository.getUserById(id)
            _userDataState.value = FeedModelState()
        } catch (e: Exception) {
            _userDataState.value = FeedModelState(error = true)
        }
    }

}