package com.urlaunched.android.network.interceptors

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

fun accessTokenInterceptor(getAuthToken: suspend () -> String?) = AccessTokenInterceptor(getAuthToken = getAuthToken)

class AccessTokenInterceptor internal constructor(private val getAuthToken: suspend () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { getAuthToken() }

        return if (token == null) {
            val defaultAuthenticatedRequest = chain.request()
                .newBuilder()
                .build()
            chain.proceed(defaultAuthenticatedRequest)
        } else {
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader(AUTH_HEADER, token)
                .build()
            chain.proceed(authenticatedRequest)
        }
    }

    companion object {
        private const val AUTH_HEADER = "Authorization"
    }
}