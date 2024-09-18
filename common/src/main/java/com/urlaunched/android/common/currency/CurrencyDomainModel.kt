package com.urlaunched.android.common.currency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CurrencyDomainModel(
    @SerialName("symbol")
    val symbol: String
)