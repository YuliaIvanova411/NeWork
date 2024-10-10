package com.example.nework.error

sealed class AppError(var code: String): RuntimeException()
class ApiError(val status: Int, code: String): AppError(code)
data object NetworkError : AppError("error_network") {
    private fun readResolve(): Any = NetworkError
}

data object UnknownError: AppError("error_unknown") {
    private fun readResolve(): Any = UnknownError
}