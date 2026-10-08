package com.retrytech.ledgeapp.utils

import androidx.annotation.Keep
import com.retrytech.ledgeapp.BuildConfig

@Keep
object Const {
    const val BASE = "https://wallapaper.aspirewebsitedesigns.com/"
    const val APIKEY = "123"
    const val BASE_URL = BASE + "api/"
    const val ITEM_URL = BASE + "public/storage/"
    const val TERMS_URL = BASE + "termsOfUse"
    const val PRIVACY_URL = BASE + "privacyPolicy"
    const val NOTIFICATION_TOPIC = "sphere"
    const val PAGINATION_COUNT = 20

    fun resolveMediaUrl(path: String?): String {
        val value = path?.trim().orEmpty()
        if (value.isEmpty()) return ""

        val isAbsolute = value.startsWith("http://", true) ||
            value.startsWith("https://", true) ||
            value.startsWith("//") ||
            value.startsWith("content://", true) ||
            value.startsWith("file://", true)

        return if (isAbsolute) value else ITEM_URL + value.trimStart('/')
    }

    fun <E> List<E>?.toArrayList(): ArrayList<E> {
        val list = ArrayList<E>()
        for (data in this ?: emptyList()) {
            list.add(data)
        }
        return list
    }

    object ApiParams {
        const val fetchSettings = "fetchSettings"
        const val fetchWallpaperByCategory = "fetchWallpaperByCategory"
        const val searchWallpaper = "searchWallpaper"
        const val fetchHomePageData = "fetchHomePageData"
        const val fetchLikedWallpaper = "fetchLikedWallpaper"
        const val trackWallpaperDownload = "trackWallpaperDownload"
        const val apikey = "apikey"
        const val category_id = "category_id"
        const val access_type = "access_type"
        const val tags = "tags"
        const val start = "start"
        const val wallpaper_ids = "wallpaper_ids"
        const val wallpaper_id = "wallpaper_id"
        const val limit = "limit"
        const val device_id = "device_id"
        const val device_brand = "device_brand"
        const val device_model = "device_model"
        const val device_manufacturer = "device_manufacturer"
        const val os_version = "os_version"
        const val app_version = "app_version"

    }

    object Key {
        const val categories = "categories"
        const val is_old = "is_old"
        const val data = "data"
        const val subscriptions = "subscriptions"
        const val admob = "admob"
        const val is_notification = "is_notification"
        const val is_premium = "is_premium"
        const val favourites = "favourites"
        const val dataList = "dataList"
        const val wallpaper = "wallpaper"
        const val notification_image = "notification_image"
        const val position = "position"
        const val LANGUAGE = "language"
        const val whatsapp_status_tree_uri = "whatsapp_status_tree_uri"
        const val review_prompt_seen = "review_prompt_seen"
        const val review_never = "review_never"
        const val review_rated = "review_rated"
        const val announcement_text = "announcement_text"
        const val featured_media_url = "featured_media_url"
        const val featured_media_type = "featured_media_type"
        const val PREF_NAME = BuildConfig.APPLICATION_ID
    }
}
