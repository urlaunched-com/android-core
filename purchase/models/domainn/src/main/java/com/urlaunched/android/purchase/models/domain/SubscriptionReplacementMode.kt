package com.urlaunched.android.purchase.models.domain

enum class SubscriptionReplacementModeTypeDomainModel {
    WITH_TIME_PRORATION,
    CHARGE_PRORATED_PRICE,
    CHARGE_FULL_PRICE,
    WITHOUT_PRORATION,
    DEFERRED
}