package com.urlaunched.android.common.response

import kotlinx.coroutines.CancellationException

sealed class Response<T : Any> {
    data class Success<T : Any>(var data: T) : Response<T>()
    data class Error<T : Any>(val error: ErrorData) : Response<T>()
}

data class ErrorData(val code: Int?, val message: String?, val errorKeys: List<String> = emptyList())

inline fun <T : Any, R : Any> Response<T>.map(convert: (T) -> R): Response<R> = try {
    when (this) {
        is Response.Error -> Response.Error(error)
        is Response.Success -> Response.Success(convert(data))
    }
} catch (e: Exception) {
    Response.Error(
        ErrorData(
            code = ErrorCodes.UNKNOWN_ERROR,
            message = e.message
        )
    )
}

inline fun <T, R : Any> T.wrapResponse(block: T.() -> R): Response<R> = runCatching(block).fold(
    onSuccess = { Response.Success(it) },
    onFailure = {
        if (it is CancellationException) {
            throw it
        } else {
            Response.Error(
                ErrorData(
                    code = null,
                    message = it.message
                )
            )
        }
    }
)

inline fun <T, R : Any> T.wrapResponseFlatten(block: T.() -> Response<R>): Response<R> = runCatching(block).fold(
    onSuccess = { it },
    onFailure = {
        if (it is CancellationException) {
            throw it
        } else {
            Response.Error(
                ErrorData(
                    code = null,
                    message = it.message
                )
            )
        }
    }
)

inline fun <T : Any> Response<T>.onSuccess(block: (T) -> Unit): Response<T> {
    if (this is Response.Success) {
        block(data)
    }

    return this
}

fun <T : Any, R : Any> Response.Error<T>.mapError(): Response<R> = Response.Error(error)

fun <T : Any> Response<T>.getOrNull(): T? = if (this is Response.Success) {
    this.data
} else {
    null
}

object ErrorCodes {
    const val INVALID_PARAMS = 422
    const val UNKNOWN_ERROR = 1024
    const val NO_AUTHORIZED = 401
    const val INTERNET_CONNECTION_ERROR = 1025
    const val JSON_VALIDATION_ERROR = 1026
}