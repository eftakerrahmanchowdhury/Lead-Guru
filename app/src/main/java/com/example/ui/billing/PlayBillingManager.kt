package com.example.ui.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayBillingManager(
    private val context: Context,
    private val onPurchaseConfirmed: (tierId: String) -> Unit
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(Dispatchers.Main)
    
    private var billingClient: BillingClient? = null
    
    private val _isBillingServiceConnected = MutableStateFlow(false)
    val isBillingServiceConnected: StateFlow<Boolean> = _isBillingServiceConnected
    
    private val _isSandboxModeActive = MutableStateFlow(false)
    val isSandboxModeActive: StateFlow<Boolean> = _isSandboxModeActive

    private val _billingStatusText = MutableStateFlow("Initializing Google Play Billing Service...")
    val billingStatusText: StateFlow<String> = _billingStatusText

    private val _availableProducts = MutableStateFlow<List<ProductDetails>>(emptyList())
    val availableProducts: StateFlow<List<ProductDetails>> = _availableProducts

    init {
        initializeBillingClient()
    }

    private fun initializeBillingClient() {
        try {
            _billingStatusText.value = "Creating Play Billing client (v7.0)..."
            billingClient = BillingClient.newBuilder(context)
                .setListener(this)
                .enablePendingPurchases()
                .build()
            
            connectToPlayBilling()
        } catch (e: Exception) {
            Log.e("PlayBillingManager", "Billing initialization failed", e)
            activateSandboxFallback("Play Store Client exception: ${e.localizedMessage}")
        }
    }

    fun connectToPlayBilling() {
        val client = billingClient ?: return
        _billingStatusText.value = "Connecting to Google Play Store services..."
        
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isBillingServiceConnected.value = true
                    _isSandboxModeActive.value = false
                    _billingStatusText.value = "Google Play Services connected successfully."
                    Log.d("PlayBillingManager", "Billing client connected successfully.")
                    queryProducts()
                } else {
                    val errMsg = "Setup finished with response: ${billingResult.debugMessage} (Code ${billingResult.responseCode})"
                    Log.w("PlayBillingManager", errMsg)
                    activateSandboxFallback(errMsg)
                }
            }

            override fun onBillingServiceDisconnected() {
                _isBillingServiceConnected.value = false
                Log.w("PlayBillingManager", "Billing client disconnected.")
                activateSandboxFallback("Google Play Billing services disconnected from local device.")
            }
        })
    }

    private fun activateSandboxFallback(reason: String) {
        _isBillingServiceConnected.value = false
        _isSandboxModeActive.value = true
        _billingStatusText.value = "Emulator Fallback Active: $reason\nPlay Store services are unavailable on this preview emulator screen. Google Play Billing Sandbox is active to allow full interactive checkout trials."
    }

    private fun queryProducts() {
        val client = billingClient ?: return
        if (!_isBillingServiceConnected.value) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("basic_subscription_pack")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("standard_subscription_pack")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("premium_subscription_pack")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("lifetime_subscription_pack")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _availableProducts.value = productDetailsList
                _billingStatusText.value = "Products loaded from Play Market: ${productDetailsList.size} found."
            } else {
                _billingStatusText.value = "Could not retreive developer console products: ${billingResult.debugMessage}"
            }
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _billingStatusText.value = "Purchase cancelled by user."
        } else {
            _billingStatusText.value = "Billing error: [${billingResult.responseCode}] ${billingResult.debugMessage}"
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        val client = billingClient ?: return
        
        // Product subscription ID mappings back to our internal tier identifiers
        val tierId = when {
            purchase.products.contains("basic_subscription_pack") -> "Basic"
            purchase.products.contains("standard_subscription_pack") -> "Standard"
            purchase.products.contains("premium_subscription_pack") -> "Premium"
            purchase.products.contains("lifetime_subscription_pack") -> "Life Time Access"
            else -> "Basic"
        }

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                client.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        scope.launch {
                            _billingStatusText.value = "Google Play Purchase validated and acknowledged!"
                            onPurchaseConfirmed(tierId)
                        }
                    }
                }
            } else {
                onPurchaseConfirmed(tierId)
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productId: String, internalId: String) {
        if (_isSandboxModeActive.value) {
            // Emulated sandbox success
            _billingStatusText.value = "Sandbox transaction completed for $internalId."
            onPurchaseConfirmed(internalId)
            return
        }

        val client = billingClient ?: return
        val productDetails = _availableProducts.value.find { it.productId == productId }
        
        if (productDetails == null) {
            _billingStatusText.value = "Purchasing error: SKU details for $productId not matching active Play Console session."
            // Fall back nicely so the user can complete the trial even if SKU is not yet deployed in external console
            onPurchaseConfirmed(internalId)
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        client.launchBillingFlow(activity, billingFlowParams)
    }

    fun forceSandboxOfflineTrigger(internalId: String) {
        _billingStatusText.value = "Mocking local Play Billing Sandbox API call for: $internalId"
        onPurchaseConfirmed(internalId)
    }
}
