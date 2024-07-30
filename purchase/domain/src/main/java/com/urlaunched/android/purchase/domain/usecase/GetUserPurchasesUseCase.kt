package com.urlaunched.android.purchase.domain.usecase

import com.urlaunched.android.common.response.Response
import com.urlaunched.android.purchase.domain.repository.PurchaseRepository
import com.urlaunched.android.purchase.models.domain.PurchaseDomainModel
import com.urlaunched.android.purchase.models.domain.PurchaseTypeDomainModel

class GetUserPurchasesUseCase(private val purchaseRepository: PurchaseRepository) {
    suspend operator fun invoke(purchaseType: PurchaseTypeDomainModel): Response<List<PurchaseDomainModel>> =
        purchaseRepository.getUserPurchases(purchaseType)
}