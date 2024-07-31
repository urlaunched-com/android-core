package com.urlaunched.android.purchase.domain.usecase

import com.urlaunched.android.purchase.domain.repository.PurchaseRepository

class AcknowledgePurchaseUseCase(private val purchaseRepository: PurchaseRepository) {
    suspend operator fun invoke(purchaseToken: String) = purchaseRepository.acknowledgePurchase(purchaseToken)
}