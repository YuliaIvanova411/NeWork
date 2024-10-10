package com.example.nework.model

data class AuthModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val isBlank: Boolean = false,
    val loggedIn: Boolean = false,
    val notLoggedIn: Boolean = false,
    val invalidLoginOrPass: Boolean = false,
)