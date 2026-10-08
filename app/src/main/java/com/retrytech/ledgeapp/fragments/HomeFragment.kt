package com.retrytech.ledgeapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.adapter.HomeCatAdapter
import com.retrytech.ledgeapp.databinding.FragmentHomeBinding
import com.retrytech.ledgeapp.model.SettingData
import com.retrytech.ledgeapp.utils.Const
import com.retrytech.ledgeapp.utils.Const.toArrayList
import com.retrytech.ledgeapp.utils.Global
import com.retrytech.ledgeapp.utils.RetrofitClient
import com.retrytech.ledgeapp.viewmodel.HomeViewModel
import com.retrytech.ledgeapp.viewmodel.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import android.text.TextUtils
import kotlin.math.abs


class HomeFragment : BaseFragment(), Runnable {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: HomeViewModel
    lateinit var mainViewModel: MainViewModel
    lateinit var handler: Handler
    var reversed = false
    var scrollingPos = 0;
    var wallListMap = HashMap<Int, List<SettingData.WallpapersItem>>()
    lateinit var disposable: CompositeDisposable
    private var homeLoadStartedAt = 0L
    private val minHomeShimmerDurationMs = 500L
    private var featuredPlayer: ExoPlayer? = null
    private var isFeaturedOverrideActive = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        disposable = CompositeDisposable()

        initView()
        initListener()
        getHomeData()
        binding.model = viewModel




        return binding.root
    }


    var scrolledByUser = false
    var letestWall: MutableList<SettingData.WallpapersItem> = mutableListOf()

    private fun getHomeData() {
        disposable.add(
            RetrofitClient.service.fetchHomePageData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe {
                    homeLoadStartedAt = System.currentTimeMillis()
                    binding.sihmmer.visibility = View.VISIBLE
                    binding.sihmmer.startShimmer()
                }
                .doOnTerminate {
                    hideHomeShimmerSafely()
                    binding.swipeRefreshHome.isRefreshing = false
                }
                .doOnError { throwable -> }
                .subscribe { homedata, throwable ->
                    if (homedata != null && homedata.status!!) {

                        viewModel.featureImagesAdapter.updateData(homedata.featuredWallpapers!!)
                        val dotlist: MutableList<String> = ArrayList()
                        for (i in viewModel.featureImagesAdapter.mList.indices) {
                            dotlist.add(" ")
                        }

                        viewModel.featureDotsAdapter.updateData(dotlist)
                        letestWall = homedata.latestWallpapers!!.toMutableList()
                        wallListMap.put(0, letestWall.toMutableList())
                        viewModel.wallpaperAdapter.updateData(homedata.latestWallpapers as MutableList<SettingData.WallpapersItem>)

                        handler.postDelayed(Runnable {
                            binding.rvDots.minimumWidth = binding.rvDots.width
                        }, 2000)


                    } else {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        )
    }


    var linearLayoutManager: LinearLayoutManager? = null
    var isLoading = false
    private fun initListener() {


        binding.swipeRefreshHome.setOnRefreshListener {
            refreshHomeFeed()
        }
        binding.swipeRefreshHome.setOnChildScrollUpCallback { _, _ ->
            binding.nestedScrollView.canScrollVertically(-1) || binding.appBar.top < 0
        }

        binding.rvImageByCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (viewModel.wallpaperAdapter.itemCount - 1 === linearLayoutManager?.findLastVisibleItemPosition() && !isLoading) {


                        fetchWallpaperByCat(true)

                    }
                }
            }
        })





        binding.rvFeatured.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    scrolledByUser = true;
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (scrolledByUser) {
                        handler.removeCallbacks(this@HomeFragment)
                        val itemPoition =
                            (binding.rvFeatured.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()

                        scrollingPos = itemPoition
                        reversed = scrollingPos + 1 > viewModel.featureImagesAdapter.itemCount - 1

                        scrollToPos(true)
                    }
                    scrolledByUser = false

                }


            }
        })


        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

            mainViewModel.cordinated_expandeed.value =
                abs(verticalOffset) - appBarLayout.totalScrollRange <= -50
        }

        viewModel.homeCatAdapter.onItemClick = object : HomeCatAdapter.OnItemClick {
            override fun onClick(item: SettingData.CategoriesItem) {

                catId = item.id!!
                if (wallListMap.contains(catId)) {

                    if (wallListMap.get(catId) != null && wallListMap.get(catId)?.isNotEmpty()!!) {
                        binding.tvNoData.visibility = View.GONE

                        if (catId == 0) {

                            var list = wallListMap.get(catId)!!
                            if (list.size > Const.PAGINATION_COUNT) {
                                viewModel.wallpaperAdapter.updateData(
                                    list.slice(0..Const.PAGINATION_COUNT - 1).toMutableList()
                                )
                            } else {
                                viewModel.wallpaperAdapter.updateData(
                                    list
                                )
                            }


                            return
                        }
                        viewModel.wallpaperAdapter.updateData(wallListMap.get(catId)!!)


                    }

                } else {


//                    if (catId == 0) {
//                        letestWall.clear()
//                        letestWall.addAll(viewModel.wallpaperAdapter.mList)
//                    }


                    noMoreData = false
                    disposable.clear()//if last category data is loading
                    fetchWallpaperByCat(false)
                }

            }
        }

    }

    var noMoreData = false

    private fun fetchWallpaperByCat(loadeMore: Boolean) {


        if (catId == 0) {

            var list = wallListMap.get(catId)!!
            if (viewModel.wallpaperAdapter.mList.size < list.size) {
                viewModel.wallpaperAdapter.loadMore(
                    list.slice(viewModel.wallpaperAdapter.mList.size..list.size - 1).toMutableList()
                )

            }

            return
        }
        if (!noMoreData) {

            binding.tvNoData.visibility = View.GONE

            if (!loadeMore) {
                viewModel.wallpaperAdapter.clear()
                binding.rvImageByCategory.adapter = null
                binding.rvImageByCategory.adapter = viewModel.wallpaperAdapter
                binding.sihmmer.visibility = View.VISIBLE
                binding.sihmmer.startShimmer()
            } else {
                binding.progressLoadMore.visibility = View.VISIBLE
//                binding.viewBottom.requestFocus()
            }


            val hashMap = HashMap<String, Any>()

            hashMap[Const.ApiParams.start] = viewModel.wallpaperAdapter.itemCount
            hashMap[Const.ApiParams.limit] = Const.PAGINATION_COUNT
            if (catId != 0) {
                hashMap[Const.ApiParams.category_id] = catId
            }

            disposable.add(RetrofitClient.service.fetchWallpaperByCategory(
                hashMap
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe {
                    isLoading = true
                }
                .doOnTerminate {
                    binding.sihmmer.stopShimmer()
                    binding.sihmmer.visibility = View.GONE
                    binding.progressLoadMore.visibility = View.GONE
                    isLoading = false
                    binding.swipeRefreshHome.isRefreshing = false
                }
                .doOnError { throwable -> }
                .subscribe { wallByCat, throwable ->
                    if (wallByCat != null && wallByCat.status!! && wallByCat.data != null) {

                        if (wallByCat.data.isEmpty()) {
                            noMoreData = true
                            if (viewModel.wallpaperAdapter.itemCount == 0) {
                                binding.tvNoData.visibility = View.VISIBLE

                            }

                        } else {
                            if (viewModel.wallpaperAdapter.itemCount == 0) {
                                wallListMap.put(catId, wallByCat.data.toMutableList())
                                viewModel.wallpaperAdapter.updateData(wallByCat.data as MutableList<SettingData.WallpapersItem>)
                            } else {
                                viewModel.wallpaperAdapter.loadMore(wallByCat.data as MutableList<SettingData.WallpapersItem>)


                            }
                        }


                    } else {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }

    var catId = 0

    private fun refreshHomeFeed() {
        noMoreData = false
        wallListMap.remove(catId)
        getHomeData()
        if (catId != 0) {
            fetchWallpaperByCat(false)
        }
    }

    private fun hideHomeShimmerSafely() {
        val elapsed = System.currentTimeMillis() - homeLoadStartedAt
        val delay = (minHomeShimmerDurationMs - elapsed).coerceAtLeast(0L)
        binding.sihmmer.postDelayed({
            binding.sihmmer.stopShimmer()
            binding.sihmmer.visibility = View.GONE
        }, delay)
    }

    private fun initView() {

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvFeatured)

        handler = Handler(Looper.getMainLooper())


        viewModel.homeCatAdapter.updateData(
            sessionManager.categories.toMutableList(),
            requireActivity().getString(R.string.new_)
        )
        binding.sihmmer.visibility = View.GONE
        binding.sihmmer.stopShimmer()
        binding.progressLoadMore.visibility = View.GONE
        binding.swipeRefreshHome.setColorSchemeResources(
            R.color.color_text_primary,
            R.color.color_theme_purple_dark
        )

        binding.rvImageByCategory.itemAnimator = null
        viewModel.wallpaperAdapter.favList =
            Global.convertStringToLis(sessionManager.getStringValue(Const.Key.favourites))
                .toArrayList()
        updateAnnouncementBar()
        applyFeaturedOverride()

    }

    private fun updateAnnouncementBar() {
        val announcementText = sessionManager.getStringValue(Const.Key.announcement_text).trim()
        if (TextUtils.isEmpty(announcementText)) {
            binding.loutAnnouncement.visibility = View.GONE
        } else {
            binding.tvAnnouncement.text = announcementText
            binding.tvAnnouncement.isSelected = true
            binding.loutAnnouncement.visibility = View.VISIBLE
        }
    }

    override fun run() {
        if (isFeaturedOverrideActive) return
        if (reversed) {
            if (scrollingPos - 1 < 0) {
                Log.i("TAG", "run: 1")
                scrollingPos += 1
                reversed = false
            } else {
                Log.i("TAG", "run: 2")
                scrollingPos -= 1
            }
        } else {
            if (scrollingPos + 1 > viewModel.featureImagesAdapter.itemCount - 1) {
                scrollingPos -= 1
                reversed = true
                Log.i("TAG", "run: 3")
            } else {
                scrollingPos += 1
                reversed = false
                Log.i("TAG", "run: 4")
            }
        }

        scrollToPos(false)


    }

    private fun scrollToPos(fromUser: Boolean) {
        if (isFeaturedOverrideActive) return


        if (!fromUser) {
            binding.rvFeatured.smoothScrollToPosition(scrollingPos)
        }
        viewModel.featureDotsAdapter.scrollToPos(scrollingPos)
        binding.rvDots.scrollToPosition(scrollingPos)
        handler.postDelayed(this, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(this)
        binding.sihmmer.stopShimmer()
        stopFeaturedOverrideVideo()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(this)
        binding.sihmmer.stopShimmer()
        stopFeaturedOverrideVideo()

    }

    override fun onResume() {
        super.onResume()
        updateAnnouncementBar()
        applyFeaturedOverride()

        Handler(Looper.getMainLooper()).postDelayed(
            kotlinx.coroutines.Runnable { refreshFavList() },
            1000
        )
        if (viewModel.featureImagesAdapter.itemCount != 0 && !isFeaturedOverrideActive) {
            handler.postDelayed(this, 3000)
        }


    }


    private fun refreshFavList() {

        Log.i("TAG", "refreshFavList: ")

        viewModel.wallpaperAdapter.refreshData(
            Global.convertStringToLis(
                sessionManager.getStringValue(
                    Const.Key.favourites
                )
            )
        )


    }

    private fun applyFeaturedOverride() {
        val featuredUrl = sessionManager.getStringValue(Const.Key.featured_media_url).trim()
        val featuredType = sessionManager.getStringValue(Const.Key.featured_media_type).trim().lowercase()

        if (TextUtils.isEmpty(featuredUrl)) {
            isFeaturedOverrideActive = false
            stopFeaturedOverrideVideo()
            binding.loutFeaturedOverride.visibility = View.GONE
            binding.rvFeatured.visibility = View.VISIBLE
            binding.tvFeaturedTitle.visibility = View.VISIBLE
            binding.loutFeaturedMeta.visibility = View.VISIBLE
            binding.imgFeaturedGradient.visibility = View.VISIBLE
            binding.loutFeaturedBottomInfo.visibility = View.VISIBLE
            return
        }

        isFeaturedOverrideActive = true
        binding.rvFeatured.visibility = View.GONE
        binding.tvFeaturedTitle.visibility = View.GONE
        binding.loutFeaturedMeta.visibility = View.GONE
        binding.imgFeaturedGradient.visibility = View.GONE
        binding.loutFeaturedBottomInfo.visibility = View.GONE
        binding.loutFeaturedOverride.visibility = View.VISIBLE

        if (featuredType == "video") {
            showFeaturedOverrideVideo(featuredUrl)
        } else {
            showFeaturedOverrideImage(featuredUrl)
        }
    }

    private fun showFeaturedOverrideImage(url: String) {
        stopFeaturedOverrideVideo()
        binding.featuredPlayerView.visibility = View.GONE
        binding.imgFeaturedOverride.visibility = View.VISIBLE
        Glide.with(this).load(url).into(binding.imgFeaturedOverride)
    }

    private fun showFeaturedOverrideVideo(url: String) {
        binding.imgFeaturedOverride.visibility = View.GONE
        binding.featuredPlayerView.visibility = View.VISIBLE

        featuredPlayer?.release()
        featuredPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.featuredPlayerView.player = featuredPlayer

        featuredPlayer?.repeatMode = Player.REPEAT_MODE_ALL
        featuredPlayer?.volume = 0f
        featuredPlayer?.setMediaItem(MediaItem.fromUri(Uri.parse(url)))
        featuredPlayer?.prepare()
        featuredPlayer?.playWhenReady = true
    }

    private fun stopFeaturedOverrideVideo() {
        featuredPlayer?.release()
        featuredPlayer = null
        if (this::binding.isInitialized) {
            binding.featuredPlayerView.player = null
        }
    }


}
