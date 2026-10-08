package com.retrytech.ledgeapp.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ActivityWallpaperByCatBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.Const.toArrayList
import com.retrytech.ledgeapp.utils.Global
import com.retrytech.ledgeapp.utils.RetrofitClient
import com.retrytech.ledgeapp.viewmodel.WallpaperByCatViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WallpaperByCatActivity : BaseActivity() {

    lateinit var binding: ActivityWallpaperByCatBinding

    var category: SettingData.CategoriesItem? = null
    lateinit var model: WallpaperByCatViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallpaper_by_cat)
        model = ViewModelProvider(this)[WallpaperByCatViewModel::class.java]

        initView()
        initListeners()
        getWallByCat(false)
        binding.model = model
    }

    var noMoreData = false

    var disposable = CompositeDisposable()

    private fun getWallByCat(loadeMore: Boolean) {
        if (!noMoreData) {

            binding.tvNoData.visibility = View.GONE

            if (!loadeMore) {
                model.wallpaperAdapter.clear()
                binding.rvImageByCategory.adapter = null
                binding.rvImageByCategory.adapter = model.wallpaperAdapter
                binding.sihmmer.visibility = View.VISIBLE
            } else {
                binding.progressLoadMore.visibility = View.VISIBLE
            }

            val hashMap = HashMap<String, Any>()

            hashMap[Const.ApiParams.start] = model.wallpaperAdapter.itemCount
            hashMap[Const.ApiParams.limit] = Const.PAGINATION_COUNT
            hashMap[Const.ApiParams.category_id] = category?.id!!

            disposable.add(
                RetrofitClient.service.fetchWallpaperByCategory(
                    hashMap
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        isLoading = true
                    }
                    .doOnTerminate {
                        isLoading = false
                        binding.sihmmer.visibility = View.GONE
                        binding.progressLoadMore.visibility = View.GONE

                    }
                    .doOnError { throwable -> }
                    .subscribe { wallByCat, throwable ->
                        if (wallByCat != null && wallByCat.status!! && wallByCat.data != null) {

                            if (wallByCat.data.isEmpty()) {
                                noMoreData = true
                                if (model.wallpaperAdapter.itemCount == 0) {
                                    binding.tvNoData.visibility = View.VISIBLE

                                }

                            } else {
                                if (model.wallpaperAdapter.itemCount == 0) {
                                    model.wallpaperAdapter.updateData(wallByCat.data as MutableList<SettingData.WallpapersItem>)
                                } else {
                                    model.wallpaperAdapter.loadMore(wallByCat.data as MutableList<SettingData.WallpapersItem>)

                                }
                            }


                        } else {
                            Toast.makeText(
                                this@WallpaperByCatActivity,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            )
        }
    }

    var linearLayoutManager: LinearLayoutManager? = null
    var isLoading = false
    private fun initListeners() {

        binding.rvImageByCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (model.wallpaperAdapter.itemCount - 1 === linearLayoutManager?.findLastVisibleItemPosition() && !isLoading) {


                        getWallByCat(true)

                    }
                }
            }
        })


        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()

    }

    private fun refreshFavList() {

//        model.wallpaperAdapter.favList =
//            Global.convertStringToLis(sessionManager.getStringValue(Const.Key.favourites))
//                .toMutableList()
//        model.wallpaperAdapter.notifyDataSetChanged()
        model.wallpaperAdapter.refreshData(
            Global.convertStringToLis(
                sessionManager.getStringValue(
                    Const.Key.favourites
                )
            )
        )
        Log.i(" onnnn set wall by cat", ": ${model.wallpaperAdapter.favList}")


    }

    override fun onResume() {
        refreshFavList()


        super.onResume()
    }

    private fun initView() {

        binding.progressLoadMore.visibility = View.GONE

        var s = intent.getStringExtra(Const.Key.data)
        if (s != null) {
            category = Gson().fromJson(s, SettingData.CategoriesItem::class.java)
            model.category = category
        } else {
            binding.tvNoData.visibility = View.VISIBLE
        }

        category?.let {

            model.wallpaperAdapter.favList =
                Global.convertStringToLis(sessionManager.getStringValue(Const.Key.favourites))
                    .toArrayList()

        }
    }
}