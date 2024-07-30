package com.urlaunched.android.purchase.models.domain

data class PurchaseDomainModel(
    val productId: String?,
    val purchaseToken: String,
    val orderId: String?
)