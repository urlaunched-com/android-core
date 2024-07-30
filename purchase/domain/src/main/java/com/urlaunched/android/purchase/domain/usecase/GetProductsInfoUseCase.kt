package com.urlaunched.android.purchase.domain.usecase

import com.urlaunched.android.purchase.domain.repository.PurchaseRepository
import com.urlaunched.android.purchase.models.domain.PurchaseTypeDomainModel

class GetProductsInfoUseCase(private val purchaseRepository: PurchaseRepository) {
    suspend operator fun invoke(productIds: List<String>, productType: PurchaseTypeDomainModel) =
        purchaseRepository.getProductsInfo(
            productIds,
            productType
        )
}