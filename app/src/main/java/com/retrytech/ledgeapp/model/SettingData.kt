package com.retrytech.ledgeapp.model

import com.google.gson.annotations.SerializedName

data class SettingData(


    @field:SerializedName("data")
    val data: DataItem? = null ?: DataItem(),

    @field:SerializedName("message")
    val message: String? = null ?: "",

    @field:SerializedName("status")
    val status: Boolean? = null ?: false,

    @field:SerializedName("subscriptionPackages")
    val subscriptionPackages: List<SubscriptionPackagesItem>? = null ?: mutableListOf(),

    @field:SerializedName("categories")
    val categories: List<CategoriesItem>? = null ?: mutableListOf(),

    @field:SerializedName("admob")
    val admob: List<AdmobItem>? = null ?: mutableListOf(),


) {
	data class WallpapersItem(

        @field:SerializedName("access_type")//0=premium,1=lock,2=free
        val accessType: Int? = null ?: 0,

        @field:SerializedName("thumbnail")
        val thumbnail: String? = null ?: "",

        @field:SerializedName("category_id")
        val categoryId: Int? = null ?: 0,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null ?: "",

        @field:SerializedName("created_at")
        val createdAt: String? = null ?: "",

        @field:SerializedName("wallpaper_type")
        val wallpaperType: Int? = null ?: 0,

        @field:SerializedName("id")
        val id: Int? = null ?: 0,

        @field:SerializedName("content")
        val content: String? = null ?: "", // could be video

        @field:SerializedName("is_featured")
        val isFeatured: Int? = null ?: 0,

        @field:SerializedName("tags")
        val tags: String? = null ?: "",

        @field:SerializedName("download_count")
        val downloadCount: Int? = null ?: 0


    )

    data class DataItem(

        @field:SerializedName("updated_at")
        val updatedAt: String? = null ?: "",

        @field:SerializedName("created_at")
        val createdAt: String? = null ?: "",

        @field:SerializedName("id")
        val id: Int? = null ?: 0,

        @field:SerializedName("app_name")
        val app_name: String? = null ?: "",

        @field:SerializedName("splash_media")
        val splashMedia: String? = null ?: "",

        @field:SerializedName("splash_media_type")
        val splashMediaType: String? = null ?: "",

        @field:SerializedName("splash_media_url")
        val splashMediaUrl: String? = null ?: "",

        @field:SerializedName("announcement_text")
        val announcementText: String? = null ?: "",

        @field:SerializedName("featured_media")
        val featuredMedia: String? = null ?: "",

        @field:SerializedName("featured_media_type")
        val featuredMediaType: String? = null ?: "",

        @field:SerializedName("featured_media_url")
        val featuredMediaUrl: String? = null ?: "",


        )

	data class CategoriesItem(

        @field:SerializedName("image")
        val image: String? = null ?: "",

        @field:SerializedName("updated_at")
        val updatedAt: String? = null ?: "",

        @field:SerializedName("created_at")
        val createdAt: String? = null ?: "",

        @field:SerializedName("id")
        val id: Int? = null ?: 0,

        @field:SerializedName("title")
        val title: String? = null ?: "",

        @field:SerializedName("type")
        val type: Int = 0,

        @field:SerializedName("wallpapers_count")
        var wallpapers_count: Int? = null ?: 0,
    )

	data class AdmobItem(

        @field:SerializedName("publisher_id")
        val publisherId: String? = null ?: "",

        @field:SerializedName("rewarded_id")
        val rewardedId: String? = null ?: "",

        @field:SerializedName("updated_at")
        val updatedAt: String? = null ?: "",

        @field:SerializedName("admob_app_id")
        val admobAppId: String? = null ?: "",

        @field:SerializedName("banner_id")
        val bannerId: String? = null ?: "",

        @field:SerializedName("created_at")
        val createdAt: String? = null ?: "",

        @field:SerializedName("id")
        val id: Int? = null ?: 0,

        @field:SerializedName("intersial_id")
        val intersialId: String? = null ?: "",

        @field:SerializedName("type")
        val type: Int? = null ?: 0
    )

	data class SubscriptionPackagesItem(

        @field:SerializedName("duration")
        val duration: Int? = null ?: 0,

        @field:SerializedName("android_product_id")
        val androidProductId: String? = null ?: "",

        @field:SerializedName("ios_product_id")
        val iosProductId: String? = null ?: "",

        @field:SerializedName("updated_at")
        val updatedAt: String? = null ?: "",

        @field:SerializedName("price")
        val price: String? = null ?: "",

        @field:SerializedName("days")
        val days: String? = null ?: "",

        @field:SerializedName("created_at")
        val createdAt: String? = null ?: "",

        @field:SerializedName("currency")
        val currency: String? = null ?: "",

        @field:SerializedName("id")
        val id: Int? = null ?: 0,

        @field:SerializedName("package_id")
        val packageId: Int? = null ?: 0
    )
}
