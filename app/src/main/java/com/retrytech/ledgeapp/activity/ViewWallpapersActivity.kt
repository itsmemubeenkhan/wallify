package com.retrytech.ledgeapp.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.github.islamkhsh.CardSliderViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.retrytech.ledgeapp.BuildConfig
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ActivityViewWallpapersBinding
import com.retrytech.ledgeapp.databinding.ItemLockedPopupBinding
import com.retrytech.ledgeapp.databinding.ItemPremiumPopupBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.MyApplication
import com.retrytech.ledgeapp.utils.RetrofitClient
import com.retrytech.ledgeapp.utils.ads.RewardAds
import com.retrytech.ledgeapp.viewmodel.ViewWallpapersModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ViewWallpapersActivity : BaseActivity() {

    lateinit var binding: ActivityViewWallpapersBinding
    lateinit var model: ViewWallpapersModel
    var dataList: List<SettingData.WallpapersItem> = arrayListOf()
    var pos = 0
    var rewardEarned = false
    private val adItemId = -99999
    private val adInsertInterval = 4
    private val adSwipeUnlockDelayMs = 2000L
    private val disposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_wallpapers)
        model = ViewModelProvider(this)[ViewWallpapersModel::class.java]

        initView()
        initListeners()
        binding.viewPager.setCurrentItem(pos, false)

        binding.model = model
    }


    private fun startDownload() {
        val currentWallpaper = dataList[binding.viewPager.currentItem]
        startDownload(currentWallpaper)
    }

    private fun startDownload(currentWallpaper: SettingData.WallpapersItem) {
        binding.loutLoaderDownload.visibility = View.VISIBLE
        downloadWall(currentWallpaper, object : OnDownload {
            override fun onComplete() {
                binding.loutLoaderDownload.visibility = View.GONE
                trackWallpaperDownload(currentWallpaper.id ?: 0)
            }

            override fun onError() {
                binding.loutLoaderDownload.visibility = View.GONE
            }
        })
    }

    private fun loadRewardAd() {

        val myApplication: MyApplication = getApplication() as MyApplication


        myApplication.rewardAd?.rewardAdListnear = object : RewardAds.RewardAdListnear {
            override fun onAdClosed() {
                Log.i("TAG", "onAdClosed: $rewardEarned")

                if (rewardEarned) {


                    startDownload()
                }
                myApplication.rewardAd?.initGoogle()
                rewardEarned = false

            }

            override fun onEarned() {
                Log.i("TAG", "onEarned: ")
                rewardEarned = true
            }

            override fun onFailed() {

                startDownload()
                myApplication.rewardAd?.initGoogle()
            }
        }
        myApplication.rewardAd?.showAd()
    }


    private fun showLockedPopUp() {
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = LayoutInflater.from(this)
            .inflate(com.retrytech.ledgeapp.R.layout.item_locked_popup, null, false)
        val binding: ItemLockedPopupBinding = DataBindingUtil.bind(view)!!
        if (binding != null) {
            binding.btnWatchAd.setOnClickListener { view2 ->
                loadRewardAd()
                dialog.dismiss()
            }
            binding.btnCancel.setOnClickListener { view3 -> dialog.dismiss() }
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    private fun showPremiumPopup() {
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = LayoutInflater.from(this).inflate(R.layout.item_premium_popup, null, false)
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

    private fun initListeners() {

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.loutLoaderDownload.setOnClickListener {

        }

        binding.viewPager.registerOnPageChangeCallback(object :
            com.github.islamkhsh.viewpager2.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentItem = dataList[position]
                if (isAdItem(currentItem)) {
                    binding.viewPager.isUserInputEnabled = false
                    binding.viewPager.postDelayed({
                        binding.viewPager.isUserInputEnabled = true
                    }, adSwipeUnlockDelayMs)
                    super.onPageSelected(position)
                    return
                }

                var img_url: String = ""
                img_url = if (dataList[position].wallpaperType == 0) {
                    dataList[position].content!!
                } else {
                    dataList[position].thumbnail!!

                }

                Glide.with(this@ViewWallpapersActivity).load(Const.resolveMediaUrl(img_url))
                    .placeholder(binding.imgBg.drawable).apply(
                        RequestOptions().error(
                            R.color.transparent
                        ).priority(Priority.HIGH)
                    ).into(binding.imgBg)
                super.onPageSelected(position)

            }

        })

    }

    private fun initView() {

        binding.viewPager.adapter = model.viewWallpapersAdapter
        setBlur(binding.blurView, binding.rootLout)
        model.viewWallpapersAdapter.onActionClick = object : com.retrytech.ledgeapp.adapter.ViewWallpapersAdapter.OnActionClick {
            override fun onDownload(item: SettingData.WallpapersItem) {
                handleDownloadClick(item)
            }

            override fun onPreview(item: SettingData.WallpapersItem) {
                if (isAdItem(item)) return
                openPreview(item)
            }

            override fun onShare(item: SettingData.WallpapersItem) {
                if (isAdItem(item)) return
                shareApp()
            }
        }

        val requestedPos = intent.getIntExtra(Const.Key.position, 0)
        var s = intent.getStringExtra(Const.Key.dataList)
        if (s != null) {
            val wallpapers: List<SettingData.WallpapersItem> = Gson().fromJson(
                s, object : TypeToken<List<SettingData.WallpapersItem>?>() {}.type
            )
            dataList = withInlineAdCards(wallpapers)
            pos = resolveInitialPosition(wallpapers, dataList, requestedPos)

            model.viewWallpapersAdapter.updateData(dataList)
            binding.viewPager.autoSlideTime = CardSliderViewPager.STOP_AUTO_SLIDING
        }

    }

    private fun isCurrentPageAd(): Boolean {
        if (dataList.isEmpty()) return false
        return isAdItem(dataList[binding.viewPager.currentItem])
    }

    private fun isAdItem(item: SettingData.WallpapersItem): Boolean {
        return item.id == adItemId
    }

    private fun handleDownloadClick(item: SettingData.WallpapersItem) {
        if (isAdItem(item)) return

        if (!sessionManager.getPremium()) {
            if (item.accessType == 2) {
                startDownload(item)
            } else if (item.accessType == 1) {
                showLockedPopUp()
            } else {
                showPremiumPopup()
            }
        } else {
            startDownload(item)
        }
    }

    private fun openPreview(item: SettingData.WallpapersItem) {
        val intent = Intent(this, PreviewActivity::class.java)
        intent.putExtra(Const.Key.wallpaper, Gson().toJson(item))
        startActivity(intent)
    }

    private fun shareApp() {
        val appLink = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, appLink)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    private fun withInlineAdCards(list: List<SettingData.WallpapersItem>): List<SettingData.WallpapersItem> {
        if (sessionManager.getPremium() || list.isEmpty()) return list
        val merged = ArrayList<SettingData.WallpapersItem>(list.size + (list.size / adInsertInterval))
        list.forEachIndexed { index, wallpaper ->
            merged.add(wallpaper)
            val shouldInsertAd = (index + 1) % adInsertInterval == 0 && index != list.lastIndex
            if (shouldInsertAd) {
                merged.add(
                    SettingData.WallpapersItem(
                        accessType = -1,
                        wallpaperType = 0,
                        id = adItemId,
                        content = "",
                        thumbnail = ""
                    )
                )
            }
        }
        return merged
    }

    private fun resolveInitialPosition(
        originalList: List<SettingData.WallpapersItem>,
        mergedList: List<SettingData.WallpapersItem>,
        requestedPos: Int
    ): Int {
        if (mergedList.isEmpty()) return 0
        if (originalList.isEmpty()) return requestedPos.coerceIn(0, mergedList.lastIndex)

        val safeOriginalPos = requestedPos.coerceIn(0, originalList.lastIndex)
        val targetId = originalList[safeOriginalPos].id
        val mappedIndex = mergedList.indexOfFirst { it.id == targetId }

        return if (mappedIndex >= 0) mappedIndex else requestedPos.coerceIn(0, mergedList.lastIndex)
    }

    private fun trackWallpaperDownload(wallpaperId: Int) {
        if (wallpaperId <= 0) return

        val payload = HashMap<String, Any>()
        payload[Const.ApiParams.wallpaper_id] = wallpaperId
        payload[Const.ApiParams.device_id] = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: ""
        payload[Const.ApiParams.device_brand] = Build.BRAND ?: ""
        payload[Const.ApiParams.device_model] = Build.MODEL ?: ""
        payload[Const.ApiParams.device_manufacturer] = Build.MANUFACTURER ?: ""
        payload[Const.ApiParams.os_version] = "Android ${Build.VERSION.RELEASE ?: ""}"
        payload[Const.ApiParams.app_version] = BuildConfig.VERSION_NAME

        disposable.add(
            RetrofitClient.service.trackWallpaperDownload(payload)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe({
                    // analytics call intentionally silent
                }, {
                    // keep download UX unaffected on analytics failure
                })
        )
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }
}
