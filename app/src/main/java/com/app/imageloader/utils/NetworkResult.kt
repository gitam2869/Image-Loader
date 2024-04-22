package com.app.imageloader.utils

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkResult<T>(data = data)
    class Error<T>(message: String?) : NetworkResult<T>(message = message)
}