package com.urlaunched.android.purchase.domain.usecase

import android.app.Activity
import com.urlaunched.android.purchase.domain.repository.PurchaseRepository
import com.urlaunched.android.purchase.models.domain.PurchaseTypeDomainModel

class MakePurchaseUseCase(
    private val purchaseRepository: PurchaseRepository
) {
    operator fun invoke(
        activity: Activity,
        productType: PurchaseTypeDomainModel,
        productId: String
    ) = purchaseRepository.makePurchase(
        activity = activity,
        productType = productType,
        productId = productId
    )
}