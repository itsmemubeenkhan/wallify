package com.retrytech.ledgeapp.utils

import com.retrytech.ledgeapp.model.FetchWallByCat
import com.retrytech.ledgeapp.model.HomeData
import com.retrytech.ledgeapp.model.SettingData
import io.reactivex.Single
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitService {

    @POST(Const.ApiParams.fetchSettings)
    fun fetchSettings(): Single<SettingData?>

    @POST(Const.ApiParams.fetchHomePageData)
    fun fetchHomePageData(): Single<HomeData?>

    @FormUrlEncoded
    @POST(Const.ApiParams.fetchWallpaperByCategory)
    fun fetchWallpaperByCategory(
        @FieldMap hashMap: HashMap<String, Any>
    ): Single<FetchWallByCat?>

    @FormUrlEncoded
    @POST(Const.ApiParams.fetchLikedWallpaper)
    fun fetchLikedWallpaper(
        @FieldMap hashMap: HashMap<String, Any>
    ): Single<FetchWallByCat?>

    @FormUrlEncoded
    @POST(Const.ApiParams.searchWallpaper)
    fun searchWallpaper(
        @FieldMap hashMap: HashMap<String, Any>
    ): Single<FetchWallByCat?>

    @FormUrlEncoded
    @POST(Const.ApiParams.trackWallpaperDownload)
    fun trackWallpaperDownload(
        @FieldMap hashMap: HashMap<String, Any>
    ): Single<SettingData?>

}
