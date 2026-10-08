package com.retrytech.ledgeapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.retrytech.ledgeapp.model.SettingData

class SessionManager @SuppressLint("CommitPrefEdits") constructor(private val context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences(Const.Key.PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun saveStringValue(key: String?, value: String?) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringValue(key: String): String {
        return pref.getString(key, "")!!
    }

    fun saveBooleanValue(key: String?, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBooleanValue(key: String?): Boolean {
        return pref.getBoolean(key, false)
    }

    fun saveIntValue(key: String?, value: Int) {
        editor.putInt(key, value)
        editor.apply()
    }

    fun getIntValue(key: String?): Int {
        return pref.getInt(key, 0)
    }

    fun clear() {
        editor.clear().apply()
    }


    fun saveSubscriptions(data: List<SettingData.SubscriptionPackagesItem?>?) {
        editor.putString(Const.Key.subscriptions, Gson().toJson(data))
        editor.apply()
    }


    fun saveAdmob(data: SettingData.AdmobItem) {
        editor.putString(Const.Key.admob, Gson().toJson(data))
        editor.apply()
    }

    fun setPremium(b: Boolean) {
        editor.putBoolean(Const.Key.is_premium, b)
        editor.apply()
    }

    fun getPremium(): Boolean {
        return pref.getBoolean(Const.Key.is_premium, false)

    }


    val subscription: List<SettingData.SubscriptionPackagesItem>
        get() {
            val str = pref.getString(Const.Key.subscriptions, "")
            return if (!str.isNullOrEmpty()) {
                Gson().fromJson(
                    str, object : TypeToken<List<SettingData.SubscriptionPackagesItem>?>() {}.type
                )
            } else arrayListOf()
        }


    fun getLanguage(): String {

        return pref.getString(Const.Key.LANGUAGE, "en")!!
    }

    fun saveLanguage(id: String) {
        editor.putString(Const.Key.LANGUAGE, id)
        editor.apply()
    }



    fun saveCategories(data: List<SettingData.CategoriesItem?>?) {
        editor.putString(Const.Key.categories, Gson().toJson(data))
        editor.apply()
    }

    val categories: List<SettingData.CategoriesItem>
        get() {
            val str = pref.getString(Const.Key.categories, "")
            return if (!str.isNullOrEmpty()) {
                Gson().fromJson(
                    str, object : TypeToken<List<SettingData.CategoriesItem>?>() {}.type
                )
            } else arrayListOf()
        }

    val admob: SettingData.AdmobItem?
        get() {
            val str = pref.getString(Const.Key.admob, "")
            return if (!str.isNullOrEmpty()) {
                Gson().fromJson(str, SettingData.AdmobItem::class.java)
            } else null
        }
}