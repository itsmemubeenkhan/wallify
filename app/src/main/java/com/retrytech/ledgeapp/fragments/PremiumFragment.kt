package com.retrytech.ledgeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.databinding.FragmentPremiumBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.Const.toArrayList
import com.retrytech.ledgeapp.utils.Global
import com.retrytech.ledgeapp.utils.RetrofitClient
import com.retrytech.ledgeapp.viewmodel.PremiumViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PremiumFragment : BaseFragment() {

    private lateinit var binding: FragmentPremiumBinding
    private lateinit var model: PremiumViewModel
    private val disposable = CompositeDisposable()
    private var noMoreData = false
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_premium, container, false)
        model = ViewModelProvider(requireActivity())[PremiumViewModel::class.java]

        initView()
        initListeners()
        loadPremium(false)

        binding.model = model
        return binding.root
    }

    private fun initView() {
        model.wallpaperAdapter.favList =
            Global.convertStringToLis(sessionManager.getStringValue(Const.Key.favourites)).toArrayList()
        binding.rvImageByCategory.setHasFixedSize(true)
        binding.rvImageByCategory.itemAnimator = null
        binding.rvImageByCategory.setItemViewCacheSize(12)
        (binding.rvImageByCategory.layoutManager as? GridLayoutManager)?.initialPrefetchItemCount = 6
    }

    private fun initListeners() {
        binding.rvImageByCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !isLoading && !noMoreData) {
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                    if (layoutManager.findLastVisibleItemPosition() >= model.wallpaperAdapter.itemCount - 1) {
                        loadPremium(true)
                    }
                }
            }
        })
    }

    private fun loadPremium(loadMore: Boolean) {
        if (!loadMore) {
            noMoreData = false
            model.wallpaperAdapter.clear()
            binding.loutNoPremium.visibility = View.GONE
            binding.sihmmer.visibility = View.VISIBLE
            binding.sihmmer.startShimmer()
        } else {
            binding.progressLoadMore.visibility = View.VISIBLE
        }

        val hashMap = HashMap<String, Any>()
        hashMap[Const.ApiParams.start] = model.wallpaperAdapter.itemCount
        hashMap[Const.ApiParams.limit] = Const.PAGINATION_COUNT
        hashMap[Const.ApiParams.access_type] = 0

        disposable.add(
            RetrofitClient.service.searchWallpaper(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe { isLoading = true }
                .doOnTerminate {
                    isLoading = false
                    binding.progressLoadMore.visibility = View.GONE
                    binding.sihmmer.stopShimmer()
                    binding.sihmmer.visibility = View.GONE
                }
                .subscribe { response, _ ->
                    if (response != null && response.status == true && response.data != null) {
                        val filtered = response.data.filter { it.accessType == 0 }
                        if (filtered.isEmpty()) {
                            noMoreData = true
                            if (model.wallpaperAdapter.itemCount == 0) {
                                binding.loutNoPremium.visibility = View.VISIBLE
                            }
                        } else {
                            binding.loutNoPremium.visibility = View.GONE
                            if (model.wallpaperAdapter.itemCount == 0) {
                                model.wallpaperAdapter.updateData(filtered.toMutableList())
                            } else {
                                model.wallpaperAdapter.loadMore(filtered.toMutableList())
                            }
                        }
                    } else {
                        Toast.makeText(requireActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }

    override fun onResume() {
        super.onResume()
        model.wallpaperAdapter.refreshData(
            Global.convertStringToLis(sessionManager.getStringValue(Const.Key.favourites))
        )
    }

    override fun onDestroyView() {
        binding.sihmmer.stopShimmer()
        disposable.clear()
        super.onDestroyView()
    }
}
