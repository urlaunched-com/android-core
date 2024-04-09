package com.urlaunched.android.network.utils

import com.urlaunched.android.common.response.ErrorCodes
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.common.response.map

private const val DEFAULT_AUTH_HEADER_KEY = "authorization"

suspend fun <T : Any> retrofit2.Response<T>.executeRequestAndTryGetAuthToken(
    header: String = DEFAULT_AUTH_HEADER_KEY
): Response<Pair<T, String>> {
    var token: String? = null
    val response = executeRequest {
        this.also { token = it.headers()[header] }
    }

    return token?.let { acquiredToken -> response.map { Pair(it, acquiredToken) } } ?: Response.Error(
        ErrorData(
            code = ErrorCodes.NO_AUTHORIZED,
            message = (response as? Response.Error)?.error?.message
        )
    )
}