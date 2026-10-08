package com.retrytech.ledgeapp.activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.ActivitySearchBinding
import com.retrytech.ledgeapp.databinding.ItemFilterPopupBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.Const.toArrayList
import com.retrytech.ledgeapp.utils.Global
import com.retrytech.ledgeapp.utils.RetrofitClient
import com.retrytech.ledgeapp.viewmodel.SearchViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchActivity : BaseActivity() {

    lateinit var binding: ActivitySearchBinding
    lateinit var model: SearchViewModel

    lateinit var dialogFilter: Dialog

    //    var allList: MutableList<SettingData.WallpapersItem> = arrayListOf()
//    var lockedList: MutableList<SettingData.WallpapersItem> = arrayListOf()
//    var premiumList: MutableList<SettingData.WallpapersItem> = arrayListOf()
//    var searchList: MutableList<SettingData.WallpapersItem> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        model = ViewModelProvider(this)[SearchViewModel::class.java]


        initView()
        initListeners()
        initObserver()
        binding.model = model

    }


    private fun initObserver() {
        model.filterType.observe(this, Observer {
            if (model.searchWord.isEmpty()) {
                binding.tvSearchWord.visibility = View.GONE
                binding.tvNoDataFor.text = getString(R.string.please_type_something)
            }
            noMoreData = false
            disposable.clear()
            getSearchData(false)
        })
    }

    private fun showFilterDialogue() {
        dialogFilter = Dialog(this)
        dialogFilter.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var binding: ItemFilterPopupBinding
        val view = LayoutInflater.from(this)
            .inflate(R.layout.item_filter_popup, null, false)
        binding = DataBindingUtil.bind(view)!!


        binding.btnAll.setOnClickListener {
            model.filterType.value = 2
            dialogFilter.dismiss()
        }
        binding.btnLocked.setOnClickListener {
            model.filterType.value = 1

            dialogFilter.dismiss()

        }

        binding.btnPremium.setOnClickListener {
            model.filterType.value = 0

            dialogFilter.dismiss()

        }
        dialogFilter.setContentView(view)
        dialogFilter.setCancelable(true)
        dialogFilter.show()
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


                        getSearchData(true)

                    }
                }
            }
        })

        binding.btnFilter.setOnClickListener {
            showFilterDialogue()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

        }


        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                model.searchWord = editable.toString()
                if (model.searchWord.isEmpty()) {
                    binding.tvSearchWord.visibility = View.GONE
                    binding.tvNoDataFor.text = getString(R.string.please_type_something)

                } else {
                    binding.tvNoDataFor.text = getString(R.string.no_data_for)
                    binding.tvSearchWord.visibility = View.VISIBLE
                    binding.tvSearchWord.text = "\"" + model.searchWord + "\""

                }

                noMoreData = false
                disposable.clear()//if another api is loading
                getSearchData(false)
            }
        })


    }


    private fun refreshFavList() {

        model.wallpaperAdapter.refreshData(
            Global.convertStringToLis(
                sessionManager.getStringValue(
                    Const.Key.favourites
                )
            )
        )

        Log.i(" onnnn set search", ": ${model.wallpaperAdapter.favList}")


    }

    override fun onResume() {
        refreshFavList()
        super.onResume()

    }


    private fun initView() {


        model.wallpaperAdapter.favList =
            Global.convertStringToLis(sessionManager.getStringValue(Const.Key.favourites))
                .toArrayList()


        binding.tvSearchWord.visibility = View.GONE
        binding.progressLoadMore.visibility = View.GONE

    }

    var disposable = CompositeDisposable()
    var noMoreData = false
    private fun getSearchData(loadeMore: Boolean) {

        if (model.searchWord.isEmpty()) {
            model.wallpaperAdapter.clear()
            binding.rvImageByCategory.adapter = null
            binding.rvImageByCategory.adapter = model.wallpaperAdapter
            binding.tvNoData.visibility = View.VISIBLE
            binding.sihmmer.visibility = View.GONE

        } else {

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
                if (model.filterType.value != 2) {
                    hashMap[Const.ApiParams.access_type] = model.filterType.value!!
                }
                hashMap[Const.ApiParams.tags] = model.searchWord

                disposable.add(
                    RetrofitClient.service.searchWallpaper(
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
                                    this@SearchActivity,
                                    getString(R.string.something_went_wrong),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                )
            }
        }

    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()

    }
}