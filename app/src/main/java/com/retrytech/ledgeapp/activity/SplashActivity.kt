package com.retrytech.ledgeapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ActivitySplashBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.MyPlayStoreBilling
import com.retrytech.ledgeapp.utils.RetrofitClient
import com.retrytech.ledgeapp.utils.SessionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding
    lateinit var disposable: CompositeDisposable
    private var splashPlayer: ExoPlayer? = null
    private var notificationWallpaperJson: String? = null
    private var notificationImageUrl: String? = null
    private var splashStartTimestamp: Long = 0L
    private val splashMinimumDurationMs: Long = 4000L
    private val videoSplashFallbackMs: Long = 7000L
    private val videoSplashVisibleAfterReadyMs: Long = 1800L
    private var hasNavigated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        disposable = CompositeDisposable()
        sessionManager = SessionManager(this)
        splashStartTimestamp = System.currentTimeMillis()
        notificationWallpaperJson = extractWallpaperJson(intent)
        notificationImageUrl = extractNotificationImage(intent)
        callApiForSettingsData()
        fetchSubscriptionDetails()

    }

    private fun fetchSubscriptionDetails() {
        var myPlayStoreBilling =
            MyPlayStoreBilling(this, object : MyPlayStoreBilling.OnPurchaseComplete {
                override fun onConnected(isConnect: Boolean) {

                }

                override fun onPurchaseResult(isPurchaseSuccess: Boolean) {

                }

                override fun onError(hasError: Boolean) {
                }
            })

        myPlayStoreBilling.isSubscriptionRunning { isPurchased ->
            sessionManager.setPremium(isPurchased)
        }
    }

    private fun callApiForSettingsData() {
        disposable.add(
            RetrofitClient.service.fetchSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnTerminate {}
                .doOnError { throwable ->
                    Log.e("SplashActivity", "Error fetching settings", throwable)
                }
                .subscribe({ getAllData ->
                    if (getAllData != null && getAllData.status == true) {
                        val isVideoSplash = applyDynamicSplash(getAllData.data)
                        getAllData.admob?.firstOrNull { it.type == 1 }?.let {
                            sessionManager.saveAdmob(it)
                        }
                        sessionManager.saveSubscriptions(getAllData.subscriptionPackages)
                        sessionManager.saveCategories(getAllData.categories)
                        sessionManager.saveStringValue(
                            Const.Key.announcement_text,
                            getAllData.data?.announcementText ?: ""
                        )
                        sessionManager.saveStringValue(
                            Const.Key.featured_media_url,
                            getAllData.data?.featuredMediaUrl?.takeIf { it.isNotBlank() }
                                ?: Const.resolveMediaUrl(getAllData.data?.featuredMedia)
                        )
                        sessionManager.saveStringValue(
                            Const.Key.featured_media_type,
                            getAllData.data?.featuredMediaType ?: ""
                        )
                        if (!isVideoSplash) {
                            navigateToMainWithMinDuration()
                        } else {
                            // If video takes too long to buffer, never block app forever.
                            binding.root.postDelayed({
                                navigateToMainWithMinDuration()
                            }, videoSplashFallbackMs)
                        }
                    } else {
                        Log.e("SplashActivity", "API returned status false or null data")
                        Toast.makeText(
                            this@SplashActivity,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, { throwable ->
                    Log.e("SplashActivity", "Subscription error", throwable)
                    Toast.makeText(
                        this@SplashActivity,
                        "Error: ${throwable.message}",
                        Toast.LENGTH_LONG
                    ).show()
                })
        )
    }

    private fun navigateToMainWithMinDuration() {
        if (hasNavigated) return
        val elapsed = System.currentTimeMillis() - splashStartTimestamp
        val delay = (splashMinimumDurationMs - elapsed).coerceAtLeast(0L)
        binding.root.postDelayed({
            if (hasNavigated) return@postDelayed
            hasNavigated = true
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            if (!notificationWallpaperJson.isNullOrEmpty()) {
                mainIntent.putExtra(Const.Key.wallpaper, notificationWallpaperJson)
            }
            if (!notificationImageUrl.isNullOrEmpty()) {
                mainIntent.putExtra(Const.Key.notification_image, notificationImageUrl)
            }
            startActivity(mainIntent)
            finish()
        }, delay)
    }

    private fun applyDynamicSplash(settingData: SettingData.DataItem?): Boolean {
        val splashUrl = resolveSplashUrl(settingData)
        if (splashUrl.isEmpty()) {
            stopVideoSplash()
            binding.splashPlayerView.visibility = View.GONE
            binding.imgSplash.visibility = View.GONE
            return false
        }

        val splashType = resolveSplashType(settingData, splashUrl)
        if (splashType == "video") {
            showVideoSplash(splashUrl)
            return true
        } else {
            showImageSplash(splashUrl)
            return false
        }
    }

    private fun resolveSplashUrl(settingData: SettingData.DataItem?): String {
        val apiUrl = settingData?.splashMediaUrl?.trim().orEmpty()
        if (apiUrl.isNotEmpty()) return apiUrl

        val mediaPath = settingData?.splashMedia?.trim().orEmpty()
        return Const.resolveMediaUrl(mediaPath)
    }

    private fun resolveSplashType(settingData: SettingData.DataItem?, splashUrl: String): String {
        val serverType = settingData?.splashMediaType?.trim()?.lowercase().orEmpty()
        if (serverType == "video" || serverType == "image") return serverType

        return if (isVideoUrl(splashUrl)) "video" else "image"
    }

    private fun isVideoUrl(url: String): Boolean {
        val value = url.lowercase()
        return value.endsWith(".mp4") ||
            value.endsWith(".mov") ||
            value.endsWith(".m4v") ||
            value.endsWith(".3gp") ||
            value.endsWith(".webm")
    }

    private fun showImageSplash(url: String) {
        stopVideoSplash()
        binding.splashPlayerView.visibility = View.GONE
        binding.imgSplash.visibility = View.VISIBLE
        Glide.with(this).load(url).into(binding.imgSplash)
    }

    private fun showVideoSplash(url: String) {
        binding.imgSplash.visibility = View.GONE
        binding.splashPlayerView.visibility = View.VISIBLE

        splashPlayer?.release()
        splashPlayer = ExoPlayer.Builder(this).build()
        binding.splashPlayerView.player = splashPlayer

        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        splashPlayer?.repeatMode = Player.REPEAT_MODE_ALL
        splashPlayer?.volume = 0f
        splashPlayer?.setMediaItem(mediaItem)
        splashPlayer?.prepare()
        splashPlayer?.playWhenReady = true
        splashPlayer?.addListener(object : Player.Listener {
            override fun onRenderedFirstFrame() {
                binding.root.postDelayed({
                    navigateToMainWithMinDuration()
                }, videoSplashVisibleAfterReadyMs)
                splashPlayer?.removeListener(this)
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                navigateToMainWithMinDuration()
                splashPlayer?.removeListener(this)
            }
        })
    }

    private fun stopVideoSplash() {
        splashPlayer?.release()
        splashPlayer = null
        binding.splashPlayerView.player = null
    }

    private fun extractWallpaperJson(incomingIntent: Intent?): String? {
        if (incomingIntent == null) return null
        val fromConstKey = incomingIntent.getStringExtra(Const.Key.wallpaper)
        if (!fromConstKey.isNullOrEmpty()) return fromConstKey

        val fromFcmData = incomingIntent.getStringExtra("wallpaper_json")
        if (!fromFcmData.isNullOrEmpty()) return fromFcmData

        val extras = incomingIntent.extras ?: return null
        val bundleValue = extras.getString("wallpaper_json")
        if (!bundleValue.isNullOrEmpty()) return bundleValue
        return null
    }

    private fun extractNotificationImage(incomingIntent: Intent?): String? {
        if (incomingIntent == null) return null
        val fromConstKey = incomingIntent.getStringExtra(Const.Key.notification_image)
        if (!fromConstKey.isNullOrEmpty()) return fromConstKey

        val fromFcmData = incomingIntent.getStringExtra("image")
        if (!fromFcmData.isNullOrEmpty()) return fromFcmData

        val extras = incomingIntent.extras ?: return null
        return extras.getString("image")
    }

    override fun onDestroy() {
        stopVideoSplash()
        super.onDestroy()
    }

}
