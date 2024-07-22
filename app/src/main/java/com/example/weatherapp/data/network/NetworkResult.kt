package com.example.weatherapp.data.network

import com.example.weatherapp.data.network.NetworkResult.Success


sealed class NetworkResult<T : Any> {
    data class Success<T : Any>(val data: T) : NetworkResult<T>()

    data class Error<T : Any>(val code: Int, val message: String) : NetworkResult<T>()

    data class Exception<T : Any>(val error: Throwable) : NetworkResult<T>()
}

suspend fun <T: Any> NetworkResult<T>.onSuccess(
    action: suspend (T) -> Unit) = apply {
    if (this is Success) {
        action(data)
    }
}

suspend fun <T : Any> NetworkResult<T>.onError(
    executable: suspend (code: Int, message: String?) -> Unit
): NetworkResult<T> = apply {
    if (this is NetworkResult.Error<T>) {
        executable(code, message)
    }
}

suspend fun <T : Any> NetworkResult<T>.onException(
    executable: suspend (e: Throwable) -> Unit
): NetworkResult<T> = apply {
    if (this is NetworkResult.Exception<T>) {
        executable(error)
    }
}