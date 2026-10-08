package com.retrytech.ledgeapp.activity


import android.app.Dialog
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.gson.Gson
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ActivityPreviewBinding
import com.retrytech.ledgeapp.databinding.ItemLockedPopupBinding
import com.retrytech.ledgeapp.databinding.ItemPremiumPopupBinding
import com.retrytech.ledgeapp.databinding.ItemSetWallpaperBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.MyApplication
import com.retrytech.ledgeapp.utils.ads.MyInterstitialAds
import com.retrytech.ledgeapp.utils.ads.RewardAds
import com.retrytech.ledgeapp.viewmodel.PreviewModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Date
import java.util.concurrent.Executors


class PreviewActivity : BaseActivity() {


    companion object {
        const val HOME = 1
        const val LOCK = 2
        const val BOTH = 3
        const val DOWNLOAD = 4
        const val APPLY = 5
    }

    lateinit var model: PreviewModel

    lateinit var binding: ActivityPreviewBinding
    lateinit var selectedWall: SettingData.WallpapersItem
    var player: ExoPlayer? = null
    var rewardEarned = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.retrytech.ledgeapp.R.layout.activity_preview)

        model = ViewModelProvider(this)[PreviewModel::class.java]


        initView()
        initListeners()


    }

    lateinit var dialogSelectr: Dialog


    private fun showWhereSelectPopup() {
        dialogSelectr = Dialog(this)
        dialogSelectr.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var binding: ItemSetWallpaperBinding
        val view = LayoutInflater.from(this)
            .inflate(R.layout.item_set_wallpaper, null, false)
        binding = DataBindingUtil.bind(view)!!


        binding.btnHome.setOnClickListener {
            applyWallPaper(HOME)
            dialogSelectr.dismiss()
        }
        binding.btnBoth.setOnClickListener {
            applyWallPaper(BOTH)

            dialogSelectr.dismiss()

        }

        binding.btnLock.setOnClickListener {
            applyWallPaper(LOCK)

            dialogSelectr.dismiss()

        }
        binding.btnCancel.setOnClickListener {
            dialogSelectr.dismiss()
        }
        dialogSelectr.setContentView(view)
        dialogSelectr.setCancelable(true)
        dialogSelectr.show()
    }


    private fun initListeners() {


        binding.btnApply.setOnClickListener {

            if (sessionManager.getPremium()) {
                processApply()

            } else {
                if (selectedWall.accessType == 2) {

                    val myApplication: MyApplication = getApplication() as MyApplication

                    myApplication.intAd?.showAds(object : MyInterstitialAds.OnShow {
                        override fun onShow() {
                            Log.i("TAG manali", "onShow: preview ")
                            myApplication.intAd?.initAds(false)

                        }

                        override fun onFailed() {
                            processApply()
                        }

                        override fun onClosed() {
                            Log.i("TAG manali", "onClosed: preview ")
                            processApply()
                        }
                    }, this)
                } else if (selectedWall.accessType == 1) {
                    showLockedPopUp(APPLY)


                } else {
                    showPremiumPopup()
                }
            }

        }
        binding.loutLoader.setOnClickListener {

        }

        binding.loutLoaderDownload.setOnClickListener {

        }

        binding.btnDownload.setOnClickListener {


            if (!sessionManager.getPremium()) {

                if (selectedWall.accessType == 2) {


                    val myApplication: MyApplication = getApplication() as MyApplication

                    myApplication.intAd?.showAds(object : MyInterstitialAds.OnShow {
                        override fun onShow() {
                            Log.i("TAG manali", "onShow: preview ")
                            myApplication.intAd?.initAds(false)

                        }

                        override fun onFailed() {
                            startDownload()
                        }

                        override fun onClosed() {
                            Log.i("TAG manali", "onClosed: preview ")

                            startDownload()
                        }
                    }, this)
                } else if (selectedWall.accessType == 1) {

                    showLockedPopUp(DOWNLOAD)

                } else {
                    showPremiumPopup()
                }


            } else {
                startDownload()
            }

        }

        binding.cardClose.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


    }


    private fun processApply() {
        if (selectedWall.wallpaperType == 0) {
            showWhereSelectPopup()

        } else {
            applyWallPaper(BOTH)
        }
    }

    private fun applyWallPaper(selectedType: Int) {

        binding.loutLoader.visibility = View.VISIBLE
        if (selectedWall.wallpaperType == 0) {
            val myExecutor = Executors.newSingleThreadExecutor()
            var wallpaperManager = WallpaperManager.getInstance(applicationContext)
            if (wallpaperManager.isWallpaperSupported && wallpaperManager.isSetWallpaperAllowed) {
                myExecutor.execute {
                    var mImage = mLoad(Const.resolveMediaUrl(selectedWall.content))
                    try {
                        val bos = ByteArrayOutputStream()
                        mImage?.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
                        val bitmapdata = bos.toByteArray()
                        val bs = ByteArrayInputStream(bitmapdata)
                        when (selectedType) {
                            BOTH -> {
                                wallpaperManager.setBitmap(mImage)

                            }

                            HOME -> {
                                wallpaperManager.setStream(
                                    bs,
                                    null,
                                    true,
                                    WallpaperManager.FLAG_SYSTEM
                                )

                            }

                            LOCK -> {
                                wallpaperManager.setStream(
                                    bs,
                                    null,
                                    true,
                                    WallpaperManager.FLAG_LOCK
                                )

                            }


                        }

                        runOnUiThread {
                            binding.loutLoader.visibility = View.GONE
                            Toast.makeText(
                                this@PreviewActivity,
                                getString(R.string.wallpaper_added_successfully), Toast.LENGTH_SHORT
                            ).show()
                        }

//                        wallpaperManager.setResource(com.retrytech.ledgeapp.R.raw.sample_image_1)
                    } catch (e: IOException) {
                        // here the errors can be logged instead of printStackTrace
                        runOnUiThread {
                            Toast.makeText(
                                this@PreviewActivity,
                                getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                            ).show()
                        }

                        e.printStackTrace()
                    }
                }


            } else {
                Toast.makeText(
                    this@PreviewActivity,
                    getString(R.string.setting_wallpaper_is_not_allowed), Toast.LENGTH_SHORT
                ).show()
            }
        } else {

            downloadFileForSet(Const.resolveMediaUrl(selectedWall.content), object : OnComplete {
                override fun onComplete() {
                    binding.loutLoader.visibility = View.GONE
                    onBackPressedDispatcher.onBackPressed()
                }

                override fun onEroor() {
                    binding.loutLoader.visibility = View.GONE
                    Toast.makeText(
                        this@PreviewActivity,
                        getString(R.string.something_went_wrong), Toast.LENGTH_SHORT
                    ).show()
                }
            })
//            downloadAndSet(selectedWall.content!!, 1)
        }
    }

    private fun downloadAndSet(media: String, type: Int) {
        val name: String
        name = if (type == 0) {
            "image.jpg"
        } else {
            "video.mp4"
        }
//        val path = getPath() + "/" + name


        var downloadID = PRDownloader.download(
            Const.resolveMediaUrl(media),
            getPathForDownload().path,
            name
        ).build()
            .setOnStartOrResumeListener { }
            .setOnCancelListener { }
            .setOnProgressListener { }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {


                }

                override fun onError(error: com.downloader.Error?) {

                    Log.i("TAG", "onError: " + error?.responseCode)


                }
            })

    }


    private fun showPremiumPopup() {
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = LayoutInflater.from(this)
            .inflate(com.retrytech.ledgeapp.R.layout.item_premium_popup, null, false)
        val binding: ItemPremiumPopupBinding = DataBindingUtil.bind(view)!!
        if (binding != null) {
            binding.btnSubscribe.setOnClickListener { view2 ->
                startActivity(Intent(this, PurchasePremiumActivity::class.java))
                dialog.dismiss()
            }
            binding.btnCancel.setOnClickListener { view3 -> dialog.dismiss() }
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    private fun showLockedPopUp(type: Int) {
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = LayoutInflater.from(this)
            .inflate(com.retrytech.ledgeapp.R.layout.item_locked_popup, null, false)
        val binding: ItemLockedPopupBinding = DataBindingUtil.bind(view)!!
        if (binding != null) {

            if (type == APPLY) {
                binding.btnWatchAd.text = this.getString(R.string.watch_ad_amp_apply)
            }
            binding.btnWatchAd.setOnClickListener { view2 ->
                loadRewardAd(type)
                dialog.dismiss()
            }
            binding.btnCancel.setOnClickListener { view3 -> dialog.dismiss() }
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    private fun loadRewardAd(type: Int) {
        val myApplication: MyApplication = getApplication() as MyApplication

        myApplication.rewardAd?.rewardAdListnear = object : RewardAds.RewardAdListnear {
            override fun onAdClosed() {
                if (rewardEarned) {

                    if (type == DOWNLOAD) {
                        startDownload()

                    } else {
                        processApply()
                    }

                }
                myApplication.rewardAd?.initGoogle()


            }

            override fun onEarned() {
                rewardEarned = true
            }

            override fun onFailed() {
                if (type == DOWNLOAD) {
                    startDownload()

                } else {
                    processApply()
                }
                myApplication.rewardAd?.initGoogle()
            }
        }
        myApplication.rewardAd?.showAd()

    }

    private fun startDownload() {
        binding.loutLoaderDownload.visibility = View.VISIBLE
        downloadWall(selectedWall, object : OnDownload {
            override fun onComplete() {
                binding.loutLoaderDownload.visibility = View.GONE

            }

            override fun onError() {
                binding.loutLoaderDownload.visibility = View.GONE
            }
        })
    }


    private fun initView() {


        if (!sessionManager.getPremium()) {
//            binding.loutLoader.visibility = View.VISIBLE
//
//            var myIntAd=MyInterstitialAds(this,true,  object : MyInterstitialAds.OnShow {
//                override fun onShow() {
//            binding.loutLoader.visibility = View.GONE
//
//                }
//
//                override fun onFailed() {
//                    binding.loutLoader.visibility = View.GONE
//
//                }
//
//                override fun onClosed() {
//                    binding.loutLoader.visibility = View.GONE
//
//                }
//            })


            binding.loutLoader.visibility = View.VISIBLE
            val myApplication: MyApplication = getApplication() as MyApplication


            myApplication.intAd?.showAds(object : MyInterstitialAds.OnShow {
                override fun onShow() {
                    Log.i("TAG manali", "onShow: preview ")

                    myApplication.intAd?.initAds(false)

                }

                override fun onFailed() {
                    runOnUiThread {

                        binding.loutLoader.visibility = View.GONE
                    }
                    Log.i("TAG", "onFailed:  preview")
                }

                override fun onClosed() {
                    Log.i("TAG manali", "onClosed: preview ")
                    runOnUiThread {

                        binding.loutLoader.visibility = View.GONE
                    }

                }
            }, this@PreviewActivity)




        }


        var s = intent.getStringExtra(Const.Key.wallpaper)
        if (s != null) {
            selectedWall = Gson().fromJson(s, SettingData.WallpapersItem::class.java)
            val notificationPreview = intent.getStringExtra(Const.Key.notification_image)


            if (selectedWall.wallpaperType == 0) {

                binding.exoPlayerView.visibility = View.GONE
                binding.img.visibility = View.VISIBLE

                setBlur(binding.blurView1, binding.rootLout)
                setBlur(binding.blurView2, binding.rootLout)
                setBlur(binding.blurView3, binding.rootLout)
                val fullImageUrl = Const.resolveMediaUrl(selectedWall.content)
                val fallbackPreviewUrl = Const.resolveMediaUrl(selectedWall.thumbnail)
                val previewUrl = if (!notificationPreview.isNullOrEmpty()) {
                    notificationPreview
                } else {
                    fallbackPreviewUrl
                }
                Glide.with(this@PreviewActivity).load(
                    fullImageUrl
                ).apply(
                    RequestOptions().error(
                        com.retrytech.ledgeapp.R.color.transparent
                    ).priority(Priority.HIGH)
                ).thumbnail(
                    Glide.with(this@PreviewActivity).load(previewUrl)
                ).into(binding.img)
            } else {
                binding.loutLoader.visibility = View.VISIBLE
                binding.btnDownload.setCardBackgroundColor(this.getColorStateList(com.retrytech.ledgeapp.R.color.color_theme_blue_50))
                binding.btnApply.setCardBackgroundColor(this.getColorStateList(com.retrytech.ledgeapp.R.color.color_theme_blue_50))
                binding.cardClose.setCardBackgroundColor(this.getColorStateList(com.retrytech.ledgeapp.R.color.color_theme_blue_50))
                initPlayer(Const.resolveMediaUrl(selectedWall.content))
            }

            val d = Date()
            var day_date: CharSequence = DateFormat.format("EEEE, dd MMMM", d.time)
            binding.dayDate.text = day_date
            var time = DateFormat.format("hh:mm", d.time)
            binding.time.text = time

        }

    }

    private fun initPlayer(s: String) {

        binding.exoPlayerView.visibility = View.VISIBLE
        binding.img.visibility = View.GONE

        binding.exoPlayerView.player?.release()
        player = ExoPlayer.Builder(this)
            .build()

        binding.exoPlayerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(s))

        player?.playWhenReady = true
        player?.repeatMode = Player.REPEAT_MODE_ALL
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.volume = 0f
        player?.play()
        player?.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    runOnUiThread {
                        binding.loutLoader.visibility = View.GONE

                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {

                Log.i("TAG", "onPlayerError: " + error)
                super.onPlayerError(error)
            }
        })

    }

    override fun onDestroy() {

        if (player != null) {
            player?.release()

        }

        super.onDestroy()
    }
}
