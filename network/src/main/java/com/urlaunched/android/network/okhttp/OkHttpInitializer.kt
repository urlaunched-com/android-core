package com.urlaunched.android.network.okhttp

import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpInitializer {
    private const val DEFAULT_TIMEOUT = 60

    fun createGeneralOkHttpClient(
        enableLogging: Boolean,
        okHttpClient: OkHttpClient? = null,
        vararg interceptors: Interceptor
    ): OkHttpClient {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        val builder = (okHttpClient?.newBuilder() ?: OkHttpClient.Builder())
            .callTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .dispatcher(dispatcher)
            .retryOnConnectionFailure(true)
            .apply {
                interceptors.forEach {
                    addInterceptor(it)
                }
            }

        if (enableLogging) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            builder.addInterceptor(
                httpLoggingInterceptor.apply {
                    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        return builder.build()
    }
}