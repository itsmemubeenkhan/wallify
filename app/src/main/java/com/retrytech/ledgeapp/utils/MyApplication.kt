package com.retrytech.ledgeapp.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.retrytech.ledgeapp.utils.ads.MyInterstitialAds
import com.retrytech.ledgeapp.utils.ads.RewardAds
import java.util.Locale

class MyApplication : Application() {
    var intAd: MyInterstitialAds? = null
    var rewardAd: RewardAds? = null
    override fun onCreate() {
        FirebaseApp.initializeApp(this)


        setLanguage()




        super.onCreate()
    }

    private fun setLanguage() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                val sessionManager = SessionManager(activity)
                val locale = Locale(sessionManager.getLanguage())
                Locale.setDefault(locale)
                val resources = activity.resources
                val configuration = resources.configuration
                configuration.setLocale(locale)
                configuration.setLayoutDirection(locale)
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                val sessionManager = SessionManager(activity)
                val locale = Locale(sessionManager.getLanguage())
                Locale.setDefault(locale)
                val resources = activity.resources
                val configuration = resources.configuration
                configuration.setLocale(locale)
                configuration.setLayoutDirection(locale)
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }

            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })

    }


    public fun initializeIntAd(context: Activity) {
        if (intAd == null) {
            intAd = MyInterstitialAds(context, false, object : MyInterstitialAds.OnShow {
                override fun onShow() {
                    intAd!!.initAds(false)
                    Log.i("TAG manali", "onShow: my app ")
                }

                override fun onFailed() {

                    Log.i("TAG", "onFailed: my application")
                }

                override fun onClosed() {
                    Log.i("TAG manali", "onClosed: my app ")

                }
            })
        }
//        else {
//            if (intAd?.used == true) {
//
//
//            }
//        }
    }

    public fun initializeRewardAd(context: Activity) {
        if (rewardAd == null) {
            rewardAd = RewardAds(context)
        }

    }

}