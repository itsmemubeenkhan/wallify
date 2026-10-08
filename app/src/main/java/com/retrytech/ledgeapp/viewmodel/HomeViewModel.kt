package com.retrytech.ledgeapp.viewmodel

import com.retrytech.ledgeapp.adapter.FeatureDotsAdapter
import com.retrytech.ledgeapp.adapter.FeatureImagesAdapter
import com.retrytech.ledgeapp.adapter.HomeCatAdapter
import com.retrytech.ledgeapp.adapter.WallpaperAdapter


open class HomeViewModel : BaseViewModel() {


    var featureImagesAdapter = FeatureImagesAdapter()
    var featureDotsAdapter = FeatureDotsAdapter()
    var homeCatAdapter = HomeCatAdapter()
    var wallpaperAdapter = WallpaperAdapter()

}