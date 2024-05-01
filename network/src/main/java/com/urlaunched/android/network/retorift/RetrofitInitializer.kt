package com.urlaunched.android.network.retorift

import com.urlaunched.android.network.okhttp.OkHttpInitializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitInitializer {
    fun createGeneralRetrofitClient(
        baseUrl: String,
        enableLogging: Boolean,
        isNullsExplicit: Boolean = false,
        okHttpClient: OkHttpClient? = null,
        vararg interceptors: Interceptor
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(createKotlinJsonConvertor(isNullsExplicit))
        .client(
            OkHttpInitializer.createGeneralOkHttpClient(
                enableLogging = enableLogging,
                interceptors = interceptors,
                okHttpClient = okHttpClient
            )
        )
        .baseUrl(baseUrl)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private fun createKotlinJsonConvertor(isNullsExplicit: Boolean): Converter.Factory {
        val json = Json {
            explicitNulls = isNullsExplicit
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return json.asConverterFactory("application/json".toMediaType())
    }
}