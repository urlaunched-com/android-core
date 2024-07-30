package com.urlaunched.android.purchase.models.domain

data class SubscriptionReplacementDomainModel(
    val oldPurchaseToken: String,
    val replacementMode: SubscriptionReplacementModeTypeDomainModel
)