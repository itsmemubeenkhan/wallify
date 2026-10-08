package com.retrytech.ledgeapp.viewmodel

import com.retrytech.ledgeapp.adapter.WallpaperAdapter
import com.retrytech.ledgeapp.model.SettingData


open class WallpaperByCatViewModel : BaseViewModel() {

    var category: SettingData.CategoriesItem? = null

    var wallpaperAdapter = WallpaperAdapter()


}