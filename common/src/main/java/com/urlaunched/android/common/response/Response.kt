package com.urlaunched.android.common.response

sealed class Response<T : Any> {
    data class Success<T : Any>(var data: T) : Response<T>()
    data class Error<T : Any>(val error: ErrorData) : Response<T>()
}

data class ErrorData(val code: Int?, val message: String?, val errorKeys: List<String> = emptyList())

fun <T : Any, R : Any> Response<T>.map(convert: (T) -> R): Response<R> = try {
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

object ErrorCodes {
    const val INVALID_PARAMS = 422
    const val UNKNOWN_ERROR = 1024
    const val NO_AUTHORIZED = 401
    const val INTERNET_CONNECTION_ERROR = 1025
    const val JSON_VALIDATION_ERROR = 1026
}