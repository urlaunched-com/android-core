package com.urlaunched.android.network.utils

import com.urlaunched.android.common.response.ErrorCodes
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.common.response.map

private const val DEFAULT_AUTH_HEADER_KEY = "authorization"

suspend fun <T : Any> retrofit2.Response<T>.executeRequestAndTryGetAuthToken(
    header: String = DEFAULT_AUTH_HEADER_KEY
): Response<Pair<T, AuthTokenWithResponseCode>> {
    var token: String? = null
    var code: Int? = null
    val response = executeRequest {
        this.also {
            token = it.headers()[header]
            code = it.code()
        }
    }

    return token?.let { acquiredToken -> response.map { Pair(it, AuthTokenWithResponseCode(acquiredToken, code)) } }
        ?: Response.Error(
            ErrorData(
                code = (response as? Response.Error)?.error?.code ?: ErrorCodes.NO_AUTHORIZED,
                message = (response as? Response.Error)?.error?.message
            )
        )
}