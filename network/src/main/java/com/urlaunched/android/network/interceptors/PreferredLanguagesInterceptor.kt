package com.urlaunched.android.network.interceptors

import android.content.Context
import androidx.core.app.LocaleManagerCompat
import okhttp3.Interceptor
import okhttp3.Response

class PreferredLanguagesInterceptor(private val context: Context, private val singleLanguage: Boolean) : Interceptor {
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
            .split(LANGUAGES_DELIMITER)
            .run {
                if (singleLanguage) {
                    firstOrNull()
                        .orEmpty()
                        .takeWhile { it != ISO_REGION_SEPARATOR }
                } else {
                    joinToString(
                        separator = LANGUAGES_DELIMITER.toString().padEnd(1)
                    ) { localeTag -> localeTag.takeWhile { it != ISO_REGION_SEPARATOR } }
                }
            }

        return chain.request().newBuilder().let { requestWithLanguagesBuilder ->
            requestWithLanguagesBuilder.addHeader(HEADER_LANGUAGES, appLocale)
            chain.proceed(requestWithLanguagesBuilder.build())
        }
    }

    companion object {
        private const val HEADER_LANGUAGES = "X-Language-Preferences"
        private const val ISO_REGION_SEPARATOR = '-'
        private const val LANGUAGES_DELIMITER = ','
    }
}