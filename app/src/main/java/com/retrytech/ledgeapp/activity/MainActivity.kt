package com.retrytech.ledgeapp.activity

import android.Manifest
import android.animation.ValueAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.messaging.FirebaseMessaging
import com.retrytech.ledgeapp.BuildConfig
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.adapter.HomePagerAdapter
import com.retrytech.ledgeapp.bottomsheet.WebBottomSheet
import com.retrytech.ledgeapp.databinding.ActivityMainBinding
import com.retrytech.ledgeapp.fragments.CategoryFragment
import com.retrytech.ledgeapp.fragments.FavouriteFragment
import com.retrytech.ledgeapp.fragments.HomeFragment
import com.retrytech.ledgeapp.fragments.PremiumFragment
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.MyPlayStoreBilling
import com.retrytech.ledgeapp.viewmodel.MainViewModel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.atomic.AtomicBoolean


class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var fragmentsList: ArrayList<Fragment>
    lateinit var viewModel: MainViewModel
    lateinit var webBottomSheet: WebBottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]


        initView()
        consentForm()
        initisteners()

        initNavbarLIstners()
        initObserver()
        checkNotificationPermission()
        handleNotificationIntent(intent)
        maybeShowReviewPopup()

        binding.model = viewModel


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(incomingIntent: Intent?) {
        val wallpaperJson = incomingIntent?.getStringExtra(Const.Key.wallpaper)
        if (!wallpaperJson.isNullOrEmpty()) {
            val notificationImage = incomingIntent.getStringExtra(Const.Key.notification_image)
                ?: incomingIntent.getStringExtra("image")
            val previewIntent = Intent(this, PreviewActivity::class.java)
            previewIntent.putExtra(Const.Key.wallpaper, wallpaperJson)
            if (!notificationImage.isNullOrEmpty()) {
                previewIntent.putExtra(Const.Key.notification_image, notificationImage)
            }
            startActivity(previewIntent)
            incomingIntent?.removeExtra(Const.Key.wallpaper)
            incomingIntent?.removeExtra(Const.Key.notification_image)
        }
    }

    fun md5(s: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                .getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(0xFF and messageDigest[i].toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
//            Logger.logStackTrace(TAG, e)
        }
        return ""
    }

    private fun consentForm() {

        val android_id: String =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        val deviceId: String = md5(android_id).uppercase()
//            mAdRequest.addTestDevice(deviceId)
//            val isTestDevice: Boolean = mAdRequest.isTestDevice(this)
        Log.v(TAG, "is Admob Test Device ? $deviceId") //to confirm it worked


        val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(deviceId)
//            .addTestDeviceHashedId("B6892DF80AA6898E58D5E08F489D5A29")
            .build()


//        Create a ConsentRequestParameters object.
        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
//            .setAdMobAppId(getString(R.string.admob_app_id))
            .build()

        var consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this@MainActivity,
                    ConsentForm.OnConsentFormDismissedListener { loadAndShowError ->
                        if (loadAndShowError != null) {
                            // Consent gathering failed.
                            Log.w(
                                TAG,
                                " jjj ${loadAndShowError.errorCode}: ${loadAndShowError.message}"
                            )
                        }

                        // Consent has been gathered.
                        if (consentInformation.canRequestAds()) {
                            initializeMobileAdsSdk()
                        }
                    }
                )
            },
            { requestConsentError ->
                // Consent gathering failed.
                Log.w(
                    TAG,
                    " hellooo ${requestConsentError.errorCode}: ${requestConsentError.message}"
                )
            })

//
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            Log.i(
                "TAG",
                ": $result"
            )
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun fetchSubscriptionDetails() {
        binding.navBar.loutLoader.visibility = View.VISIBLE

        val myPlayStoreBilling =
            MyPlayStoreBilling(this, object : MyPlayStoreBilling.OnPurchaseComplete {
                override fun onConnected(isConnect: Boolean) {

                }

                override fun onPurchaseResult(isPurchaseSuccess: Boolean) {

                }

                override fun onError(hasError: Boolean) {
                }
            })
        myPlayStoreBilling.isSubscriptionRunning { isPurchased ->
            if (isPurchased) {
                sessionManager.setPremium(true)
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.subscriptions_restored_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sessionManager.setPremium(false)
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.there_is_no_subscriptions),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        binding.navBar.loutLoader.visibility = View.GONE


    }

    private fun initNavbarLIstners() {


        binding.navBar.loutLanguage.setOnClickListener { view ->
            startActivity(
                Intent(
                    this,
                    LanguageActivity::class.java
                )
            )
        }

        binding.navBar.btnRestore.setOnClickListener {
            fetchSubscriptionDetails()
        }

        binding.navBar.switchNoti.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic(Const.NOTIFICATION_TOPIC)
                sessionManager.saveBooleanValue(Const.Key.is_notification, true)

            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Const.NOTIFICATION_TOPIC)
                sessionManager.saveBooleanValue(Const.Key.is_notification, false)

            }
        }

        binding.navBar.btnRate.setOnClickListener { v ->
            closeDrawer()
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                )
            )
        }



        binding.navBar.blurView.setOnClickListener {
            closeDrawer()
        }

        binding.navBar.btnPremium.setOnClickListener {
            closeDrawer()
            startActivity(Intent(this, PurchasePremiumActivity::class.java))
        }

        binding.navBar.btnPrivacy.setOnClickListener { v ->
            closeDrawer()
            webBottomSheet = WebBottomSheet(1)
            if (!webBottomSheet.isAdded) {
                webBottomSheet.show(
                    supportFragmentManager,
                    webBottomSheet.javaClass.simpleName
                )
            }
        }

        binding.navBar.btnTerms.setOnClickListener { v ->
            closeDrawer()
            webBottomSheet = WebBottomSheet(2)
            if (!webBottomSheet.isAdded) {
                webBottomSheet.show(
                    supportFragmentManager,
                    webBottomSheet.javaClass.simpleName
                )
            }
        }

    }

    private fun closeDrawer() {
        binding.navBar.blurView.visibility = View.GONE
        binding.drawerLout.closeDrawer(GravityCompat.START)

    }

    private fun maybeShowReviewPopup() {
        if (sessionManager.getBooleanValue(Const.Key.review_never)) return
        if (sessionManager.getBooleanValue(Const.Key.review_rated)) return
        binding.rootLout.postDelayed({
            if (isFinishing || isDestroyed) return@postDelayed
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottomsheet_review, null, false)
            dialog.setContentView(view)
            view.findViewById<View>(R.id.btn_never).setOnClickListener {
                sessionManager.saveBooleanValue(Const.Key.review_never, true)
                dialog.dismiss()
            }
            view.findViewById<View>(R.id.btn_not_now).setOnClickListener {
                dialog.dismiss()
            }
            view.findViewById<View>(R.id.btn_rate_now).setOnClickListener {
                sessionManager.saveBooleanValue(Const.Key.review_rated, true)
                dialog.dismiss()
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                    )
                )
                Toast.makeText(this, getString(R.string.thanks_for_review), Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }, 5000)
    }

    private fun initObserver() {
        viewModel.cordinated_expandeed.observe(this, Observer {
            if (it) {
                binding.logoImg.visibility = View.GONE
            } else {
                binding.logoImg.visibility = View.VISIBLE

            }
        })


        viewModel.selectedNav.observe(this) {

            binding.drawerLout.closeDrawer(GravityCompat.START)

        }

        viewModel.selectedTab.observe(this) {
            binding.vPager.setCurrentItem(it, false)
            Log.i(TAG, "initObserver: " + it)
            removeBackgroundTint()
            when (it) {
                0 -> {

                    if (viewModel.cordinated_expandeed.value!!) {
                        binding.logoImg.visibility = View.GONE
                    } else {
                        binding.logoImg.visibility = View.VISIBLE

                    }


                    var value = 0
                    value = if (binding.parentHome.width == 0) {
                        300
                    } else {
                        binding.parentHome.width
                    }

                    val anim = ValueAnimator.ofInt(value)
                    anim.addUpdateListener { animator ->

                        val v = animator.animatedValue as Int

                        val layoutParams: ViewGroup.LayoutParams = binding.loutHome.layoutParams
                        layoutParams.width = v
                        layoutParams.height = MATCH_PARENT
                        binding.loutHome.layoutParams = layoutParams
                        binding.loutHome.backgroundTintList =
                            ContextCompat.getColorStateList(this, R.color.color_theme_blue)
                    }
                    anim.duration = 200
                    anim.start()

                }

                1 -> {

                    binding.logoImg.visibility = View.VISIBLE


                    val anim = ValueAnimator.ofInt(binding.parentCat.width)
                    anim.addUpdateListener { animator ->

                        val v = animator.animatedValue as Int

                        val layoutParams: ViewGroup.LayoutParams = binding.loutCat.layoutParams
                        layoutParams.width = v
                        layoutParams.height = MATCH_PARENT
                        binding.loutCat.layoutParams = layoutParams
                        binding.loutCat.backgroundTintList =
                            ContextCompat.getColorStateList(this, R.color.color_theme_blue)
                    }
                    anim.duration = 200
                    anim.start()

                }

                2 -> {

                    binding.logoImg.visibility = View.VISIBLE


                    val anim = ValueAnimator.ofInt(binding.parentFav.width)
                    anim.addUpdateListener { animator ->

                        val v = animator.animatedValue as Int
                        val layoutParams: ViewGroup.LayoutParams = binding.loutFav.layoutParams
                        layoutParams.width = v
                        layoutParams.height = MATCH_PARENT
                        binding.loutFav.layoutParams = layoutParams
                        binding.loutFav.backgroundTintList =
                            ContextCompat.getColorStateList(this, R.color.color_theme_blue)
                    }
                    anim.duration = 200
                    anim.start()

                }

                3 -> {

                    binding.logoImg.visibility = View.VISIBLE


                    val anim = ValueAnimator.ofInt(binding.parentPremium.width)
                    anim.addUpdateListener { animator ->

                        val v = animator.animatedValue as Int
                        val layoutParams: ViewGroup.LayoutParams = binding.loutPremium.layoutParams
                        layoutParams.width = v
                        layoutParams.height = MATCH_PARENT
                        binding.loutPremium.layoutParams = layoutParams
                        binding.loutPremium.backgroundTintList =
                            ContextCompat.getColorStateList(this, R.color.color_theme_blue)
                    }
                    anim.duration = 200
                    anim.start()

                }
            }

        }
    }


    override fun onBackPressed() {
        if (viewModel.navOpen.value!!) {
            viewModel.navOpen.value = false
        } else {

            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
    }


    private fun initisteners() {

        binding.loutLoader.setOnClickListener {

        }
        binding.drawerLout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.i("TAG", "onDrawerSlide: $slideOffset")
                if (slideOffset < 1) {
                    binding.navBar.blurView.visibility = View.GONE

                }
                if (slideOffset > 0.95) {
                    binding.navBar.blurView.visibility = View.VISIBLE

                }
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
        viewModel.navOpen.observe(this) {
            if (it) {
                binding.drawerLout.openDrawer(GravityCompat.START)
                if (sessionManager.getPremium()) {
                    binding.navBar.loutPurchase.visibility = View.GONE
                    binding.navBar.btnRestore.visibility = View.GONE
                } else {
                    binding.navBar.loutPurchase.visibility = View.VISIBLE
                    binding.navBar.btnRestore.visibility = View.VISIBLE
                }

            } else {
                binding.drawerLout.closeDrawer(GravityCompat.START)

            }
        }

        binding.btnSearch.setOnClickListener(View.OnClickListener {

            startActivity(Intent(this, SearchActivity::class.java))
        })

    }

    private fun removeBackgroundTint() {


        val layoutParams1: ViewGroup.LayoutParams = binding.loutHome.layoutParams
        layoutParams1.width = 100

        binding.loutHome.layoutParams = layoutParams1
        binding.loutHome.backgroundTintList = null

        val layoutParams2: ViewGroup.LayoutParams = binding.loutCat.layoutParams
        layoutParams2.width = 100

        binding.loutCat.layoutParams = layoutParams2
        binding.loutCat.backgroundTintList = null

        val layoutParams3: ViewGroup.LayoutParams = binding.loutFav.layoutParams
        layoutParams3.width = 100

        binding.loutFav.layoutParams = layoutParams3
        binding.loutFav.backgroundTintList = null

        val layoutParams4: ViewGroup.LayoutParams = binding.loutPremium.layoutParams
        layoutParams4.width = 100

        binding.loutPremium.layoutParams = layoutParams4
        binding.loutPremium.backgroundTintList = null


    }

    private fun initView() {


        binding.drawerLout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent))
        binding.drawerLout.setDrawerElevation(0f)

        binding.vPager.isUserInputEnabled = false

        setBlur(binding.blurView, binding.rootLout)

        fragmentsList = ArrayList<Fragment>()


        fragmentsList.add(HomeFragment())
        fragmentsList.add(CategoryFragment())
        fragmentsList.add(FavouriteFragment())
        fragmentsList.add(PremiumFragment())


        val adapter = HomePagerAdapter(
            supportFragmentManager,
            lifecycle
        )
        adapter.fragments = fragmentsList
        binding.vPager.adapter = adapter

        setBlur(binding.navBar.blurView, binding.rootLout)


        if (!sessionManager.getBooleanValue(Const.Key.is_old)) {
            FirebaseMessaging.getInstance().subscribeToTopic(Const.NOTIFICATION_TOPIC)
            sessionManager.saveBooleanValue(Const.Key.is_old, true)
            sessionManager.saveBooleanValue(Const.Key.is_notification, true)
        }

        binding.navBar.switchNoti.isChecked =
            sessionManager.getBooleanValue(Const.Key.is_notification)

        if (sessionManager.getPremium()) {
            binding.navBar.loutPurchase.visibility = View.GONE
            binding.navBar.btnRestore.visibility = View.GONE
        } else {
            binding.navBar.loutPurchase.visibility = View.VISIBLE
            binding.navBar.btnRestore.visibility = View.VISIBLE
        }

//

    }

    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this)

        // TODO: Request an ad.
        // InterstitialAd.load(...)
    }

}
