package com.urlaunched.android.common.currency

import kotlinx.serialization.json.Json

object CurrencyUtil {
    fun getSymbolForIsoCode(isoCode: String): String {
        val json = Json {
            ignoreUnknownKeys = true
        }

        val currencyMap by lazy {
            json.decodeFromString<Map<String, CurrencyDomainModel>>(currencyJson)
        }

        return currencyMap[isoCode]?.symbol ?: isoCode
    }
}