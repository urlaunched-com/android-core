package com.urlaunched.android.purchase.models.domain

data class ProductDomainModel(
    val id: String,
    val name: String,
    val priceFormattedString: String,
    val price: Double,
    val currencyIsoCode: String
)