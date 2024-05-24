package com.urlaunched.android.purchase.data.repository

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.urlaunched.android.common.response.ErrorData
import com.urlaunched.android.common.response.Response
import com.urlaunched.android.purchase.domain.errors.UserCancelError
import com.urlaunched.android.purchase.domain.repository.PurchaseRepository
import com.urlaunched.android.purchase.models.domain.ProductDomainModel
import com.urlaunched.android.purchase.models.domain.PurchaseTypeDomainModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class PurchaseRepositoryImpl : PurchaseRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var billingClient: BillingClient? = null
    private var productList: List<ProductDetails>? = null

    private val _purchaseCompleted: MutableStateFlow<Result<String>?> = MutableStateFlow(null)
    override val purchaseCompleted: StateFlow<Result<String>?> = _purchaseCompleted

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    completePurchase(purchase)
                }
            } else {
                onPurchaseError(billingResult)
            }
        }

    override fun billingSetup(activity: Activity, onSuccess: () -> Unit, onError: () -> Unit) {
        if (billingClient?.isReady == true) {
            return
        }

        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onSuccess()
                } else {
                    onError()
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    override fun makePurchase(activity: Activity, productType: PurchaseTypeDomainModel, productId: String) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    productList?.find { it.productId == productId }?.let {
                        if (productType == PurchaseTypeDomainModel.IN_APP_PURCHASE) {
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(it)
                                .build()
                        } else {
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(it)
                                .setOfferToken(it.subscriptionOfferDetails?.first()?.offerToken.orEmpty())
                                .build()
                        }
                    }
                )
            )
            .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    override suspend fun getProductsInfo(productIds: List<String>, productType: PurchaseTypeDomainModel) = try {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map { productId ->
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType.toBillingType())
                        .build()
                }
            ).build()

        val products = billingClient?.queryProductDetails(queryProductDetailsParams).let { result ->
            result?.productDetailsList
        }

        productList = products

        Response.Success(
            products?.mapNotNull { product ->
                if (productType == PurchaseTypeDomainModel.IN_APP_PURCHASE) {
                    product.toProductDomainModel()
                } else {
                    product.toSubscriptionDomainModelModel()
                }
            }.orEmpty()
        )
    } catch (exception: Exception) {
        if (exception is CancellationException) {
            throw exception
        } else {
            Response.Error(ErrorData(message = exception.message.toString(), code = null))
        }
    }

    private fun Long.toDoublePrice() = this / 1000000.0

    private fun ProductDetails.toProductDomainModel() = oneTimePurchaseOfferDetails?.let { oneTimeProduct ->
        ProductDomainModel(
            id = productId,
            name = name,
            priceFormattedString = oneTimeProduct.formattedPrice,
            price = oneTimeProduct.priceAmountMicros.toDoublePrice(),
            currencyIsoCode = oneTimeProduct.priceCurrencyCode
        )
    }

    private fun ProductDetails.toSubscriptionDomainModelModel(): ProductDomainModel? {
        return subscriptionOfferDetails?.firstOrNull()?.let { subscriptionOffer ->
            subscriptionOffer.pricingPhases.pricingPhaseList.firstOrNull()?.let { pricingPhase ->
                ProductDomainModel(
                    id = productId,
                    name = name,
                    priceFormattedString = pricingPhase.formattedPrice,
                    price = pricingPhase.priceAmountMicros.toDoublePrice(),
                    currencyIsoCode = pricingPhase.priceCurrencyCode
                )
            }
        }
    }

    private fun completePurchase(item: Purchase) {
        if (item.purchaseState == Purchase.PurchaseState.PURCHASED) {
            _purchaseCompleted.value = Result.success(item.purchaseToken)
            consumePurchase(item)
        }
    }

    private fun onPurchaseError(billingResult: BillingResult) {
        val result = billingResult.toKotlinResult()
        _purchaseCompleted.value = result
    }

    private fun consumePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        coroutineScope.launch {
            billingClient?.consumePurchase(consumeParams)
        }
    }

    private fun BillingResult.toKotlinResult(): Result<String> = when (responseCode) {
        BillingResponseCode.USER_CANCELED -> Result.failure(UserCancelError)
        BillingResponseCode.OK -> Result.success(debugMessage)
        else -> Result.failure(Exception())
    }

    private fun PurchaseTypeDomainModel.toBillingType(): String =
        when (this) {
            PurchaseTypeDomainModel.IN_APP_PURCHASE -> ProductType.INAPP
            PurchaseTypeDomainModel.SUBSCRIPTION -> ProductType.SUBS
        }
}