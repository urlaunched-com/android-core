package com.urlaunched.android.network.interceptors

import android.content.Context
import androidx.core.app.LocaleManagerCompat
import okhttp3.Interceptor
import okhttp3.Response

class AppLanguagesInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val appLocale = (
            LocaleManagerCompat
                .getApplicationLocales(context)
                .toLanguageTags()
                .takeIf { it.isNotEmpty() }
                ?: LocaleManagerCompat
                    .getSystemLocales(context)
                    .toLanguageTags()
            )
            .split(',')
            .joinToString(separator = ", ") { localeTag -> localeTag.takeWhile { it != '-' } }

        return chain.request().newBuilder().let { requestWithLanguagesBuilder ->
            requestWithLanguagesBuilder.addHeader(HEADER_LANGUAGES, appLocale)
            chain.proceed(requestWithLanguagesBuilder.build())
        }
    }

    companion object {
        private const val HEADER_LANGUAGES = "X-Language-Preferences"
    }
}