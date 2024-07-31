package com.urlaunched.android.purchase.domain.repository

import android.app.Activity
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.purchase.models.domain.ProductDomainModel
import com.urlaunched.android.purchase.models.domain.PurchaseDomainModel
import com.urlaunched.android.purchase.models.domain.PurchaseTypeDomainModel
import com.urlaunched.android.purchase.models.domain.SubscriptionReplacementDomainModel
import kotlinx.coroutines.flow.StateFlow

interface PurchaseRepository {
    val purchaseCompleted: StateFlow<Result<String>?>

    fun billingSetup(activity: Activity, onSuccess: () -> Unit, onError: () -> Unit = {})
    fun makePurchase(
        activity: Activity,
        productType: PurchaseTypeDomainModel,
        productId: String,
        subscriptionReplacementMode: SubscriptionReplacementDomainModel? = null
    )

    suspend fun getProductsInfo(
        productIds: List<String>,
        productType: PurchaseTypeDomainModel
    ): Response<List<ProductDomainModel>>

    suspend fun getUserPurchases(purchaseType: PurchaseTypeDomainModel): Response<List<PurchaseDomainModel>>
    suspend fun acknowledgePurchase(purchaseToken: String): Response<Unit>
}