package com.urlaunched.android.purchase.domain.usecase

import android.app.Activity
import com.urlaunched.android.purchase.domain.repository.PurchaseRepository

class BillingSetupUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(activity: Activity, onSuccess: () -> Unit, onError: () -> Unit = {}) {
        purchaseRepository.billingSetup(activity, onSuccess, onError)
    }
}