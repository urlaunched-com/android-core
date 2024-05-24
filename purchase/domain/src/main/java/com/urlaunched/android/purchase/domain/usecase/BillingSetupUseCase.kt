package com.urlaunched.android.purchase.domain.usecase

import android.app.Activity
import com.urlaunched.android.purchase.domain.repository.PurchaseRepository
import com.urlaunched.android.purchase.models.domain.PurchaseTypeDomainModel

class BillingSetupUseCase (
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(activity: Activity, onSuccess: () -> Unit, onError: () -> Unit = {}) {
        purchaseRepository.billingSetup(activity, onSuccess, onError)
    }
}