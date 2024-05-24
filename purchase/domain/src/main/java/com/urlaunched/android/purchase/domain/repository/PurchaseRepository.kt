package com.urlaunched.android.purchase.domain.repository

import android.app.Activity
import com.urlaunched.android.purchase.models.domain.ProductDomainModel
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.purchase.models.domain.PurchaseTypeDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PurchaseRepository {
    val purchaseCompleted: StateFlow<Result<String>?>

    fun billingSetup(activity: Activity, onSuccess: () -> Unit, onError: () -> Unit = {})
    fun makePurchase(activity: Activity, productType: PurchaseTypeDomainModel, productId: String)
    suspend fun getProductsInfo(productIds: List<String>, productType: PurchaseTypeDomainModel): Response<List<ProductDomainModel>>
}