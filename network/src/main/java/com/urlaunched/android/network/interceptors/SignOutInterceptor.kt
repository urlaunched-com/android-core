package com.urlaunched.android.network.interceptors

import com.urlaunched.android.common.response.ErrorCodes
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

fun signOutInterceptor(onNotAuthenticated: suspend () -> Unit) =
    SignOutInterceptor(onNotAuthenticated = onNotAuthenticated)

class SignOutInterceptor internal constructor(private val onNotAuthenticated: suspend () -> Unit) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == ErrorCodes.NO_AUTHORIZED) {
            runBlocking { onNotAuthenticated() }
        }
        return response
    }
}