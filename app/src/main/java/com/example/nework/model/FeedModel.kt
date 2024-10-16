package com.example.nework.model

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)

data class UsersModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)

data class JobModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)

data class SignInModelState(
    val signInError: Boolean = false,
    val signInWrong: Boolean = false,
)

data class RegisterModelState(
    val signUpError: Boolean = false,
    val signUpWrong: Boolean = false,
)
