package com.retrytech.ledgeapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.BillingResult
import com.google.common.collect.ImmutableList
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ActivityPurchasePremiumBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.MyPlayStoreBilling
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.Executors

class PurchasePremiumActivity : BaseActivity() {

    lateinit var binding: ActivityPurchasePremiumBinding
    lateinit var listOfSubs: List<SettingData.SubscriptionPackagesItem>
    lateinit var myPlayStoreBilling: MyPlayStoreBilling


    var disposable = CompositeDisposable()
    lateinit var selectedPackage: SettingData.SubscriptionPackagesItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_premium)


        initView()
        initListeners()


    }

    private fun initListeners() {

        binding.btnSubscribe.setOnClickListener {
            if (selectedPackage != null && selectedPackage.androidProductId!!.isNotEmpty()) {
                binding.frameLout.visibility = View.VISIBLE
                myPlayStoreBilling.startPurchase(
                    selectedPackage.androidProductId,
                    BillingClient.ProductType.SUBS,
                    false
                )

            } else {

                Toast.makeText(
                    this@PurchasePremiumActivity,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()


            }
        }

        binding.frameLout.setOnClickListener { v -> }


        binding.cardYearly.setOnClickListener { view ->

            binding.selectedMonthly = false
            selectedPackage = listOfSubs[1]

        }

        binding.cardMonthly.setOnClickListener { view ->

            binding.selectedMonthly = true

            selectedPackage = listOfSubs[0]

        }

        binding.imgBack.setOnClickListener { view -> onBackPressed() }

    }


    fun getMonthlyData(productid: String?) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productid!!)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()
        myPlayStoreBilling.billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { billingResult, productDetailsList ->
                    Log.i("TAG", "onProductDetailsResponse:  $productDetailsList")
                    if (productDetailsList.isNotEmpty()) {
                        val productDetails = productDetailsList[0]
                        Log.i("TAG", "onConnected:  inside service$productDetails")
                        runOnUiThread {
                            binding.tvMonthPrice.text =
                                productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases
                                    ?.pricingPhaseList?.get(0)?.formattedPrice ?: ""
                        }
                    } else {
                        runOnUiThread {
                            binding.tvMonthPrice.text = listOfSubs[0].price + "$"
                        }
                    }
        }
    }

    fun getYearlyData(productid: String?) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                ImmutableList.of(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productid!!)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()
        myPlayStoreBilling.billingClient.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { billingResult, list ->
            Log.i("TAG", "onProductDetailsResponse:  $list")
            if (!list.isEmpty()) {
                Log.i("TAG", "onConnected:  inside service" + list[0])
                runOnUiThread {
                    binding.tvYearPrice.text =
                        list[0].subscriptionOfferDetails!![0].pricingPhases
                            .pricingPhaseList[0].formattedPrice

                }
            } else {
                runOnUiThread {
                    binding.tvYearPrice.text = listOfSubs[1].price + "$"
                }
            }
        }
    }


    private fun initView() {


        listOfSubs = sessionManager.subscription
        selectedPackage = listOfSubs[1]







        myPlayStoreBilling =
            MyPlayStoreBilling(this, object : MyPlayStoreBilling.OnPurchaseComplete {
                override fun onConnected(isConnect: Boolean) {

                    val executorService = Executors.newSingleThreadExecutor()
                    executorService.execute {

                        getMonthlyData(listOfSubs[0].androidProductId)
                        getYearlyData(listOfSubs[1].androidProductId)

                        //  {"productId":"com.retrytech.lifesound.monthly","type":"subs",
//  "title":"Monthly (com.retrytech.life_sound (unreviewed))",
//  "name":"Monthly","localizedIn":["en-US"],
//  "skuDetailsToken":"AEuhp4KKrfW7cH8c2Ekj357jDybqm-K9iOB5srYGXUYnOqobuVQ8OziQtVVp1JzMOpM_",
//  "subscriptionOfferDetails":[
//  {"offerIdToken":"AUj\/YhhuvieIq5QUjfLE8QK3XbmPpQ41DUAH38flbmvZi2JEsEfJW\/HcaWymat4aQwZj9pvijKCYQEmTavDxKHhgn4nPwXhXf2YmKWDt+ZNpkUwrCyoMw9+9Ug==",
//  "basePlanId":"monthly-base",
//  "pricingPhases":[
//  {"priceAmountMicros":490000000,"priceCurrencyCode":"INR","formattedPrice":"₹490.00",
//  "billingPeriod":"P1M","recurrenceMode":1}
//  ],
//  "offerTags":[
//  ]}
//  ]}'


                    }
                }

                override fun onPurchaseResult(isPurchaseSuccess: Boolean) {

                    binding.frameLout.visibility = View.GONE

                    if (isPurchaseSuccess) {

                        sessionManager.setPremium(true)
                        startActivity(
                            Intent(
                                this@PurchasePremiumActivity,
                                SplashActivity::class.java
                            )
                        )
                        finishAffinity()

                    } else {

                        runOnUiThread {
                            binding.frameLout.visibility = View.GONE
                            Toast.makeText(
                                this@PurchasePremiumActivity,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }

                override fun onError(hasError: Boolean) {
                    if (hasError) {
                        runOnUiThread {
                            binding.frameLout.visibility = View.GONE
                            Toast.makeText(
                                this@PurchasePremiumActivity,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                }
            })


    }
}