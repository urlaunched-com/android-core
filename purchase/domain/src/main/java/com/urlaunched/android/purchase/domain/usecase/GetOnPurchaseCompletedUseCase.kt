package com.urlaunched.android.purchase.domain.usecase

import com.urlaunched.android.purchase.domain.repository.PurchaseRepository

class GetOnPurchaseCompletedUseCase (
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke() = purchaseRepository.purchaseCompleted
}