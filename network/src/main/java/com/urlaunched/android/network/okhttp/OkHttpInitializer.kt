package com.urlaunched.android.network.okhttp

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpInitializer {
    private const val DEFAULT_TIMEOUT_SECONDS = 60L

    fun createGeneralOkHttpClient(
        enableLogging: Boolean,
        okHttpClient: OkHttpClient? = null,
        timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
        vararg interceptors: Interceptor
    ): OkHttpClient {
        val builder = (okHttpClient?.newBuilder() ?: OkHttpClient.Builder())
            .callTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
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