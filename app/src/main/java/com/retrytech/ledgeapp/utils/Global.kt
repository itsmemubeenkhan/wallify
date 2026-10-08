package com.retrytech.ledgeapp.utils

import android.text.TextUtils
import com.retrytech.ledgeapp.model.Language
import java.text.DecimalFormat

object Global {




    @JvmStatic
    fun prettyCount(number: Number): String {
        return try {
            val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
            val numValue = number.toLong()
            val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
            val base = value / 3
            if (value >= 3 && base < suffix.size) {
                val value2 = numValue / Math.pow(10.0, (base * 3).toDouble())
                if (value2.toString().contains(".")) {
                    val num =
                        value2.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[value2.toString().split("\\.".toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray().size - 1]
                    if (num.contains("0")) {
                        DecimalFormat("#0").format(value2) + suffix[base]
                    } else {
                        DecimalFormat("#0.0").format(value2) + suffix[base]
                    }
                } else {
                    DecimalFormat("#0").format(value2) + suffix[base]
                }
            } else {
                DecimalFormat("#,##0").format(numValue)
            }
        } catch (e: Exception) {
            number.toString()
        }
    }


    fun listOfIntegerToString(list: List<Int>): String? {
        return TextUtils.join(",", list)
    }

    fun convertStringToLis(s: String): List<Int> {


        if (s.isEmpty()) {
            return listOf()
        }
        val stringList = s.split(",")

        // Convert each string in the list to an Int.
        return stringList.map { it.toInt() }


    }

    @JvmStatic
    fun getLanguages(): List<Language> {
        val list = mutableListOf<Language>()

        list.add(Language("Arabic", "العربية", "ar"))
        list.add(Language("Chinese(Simplified)", "简体中文）", "zh"))
        list.add(Language("English", "English", "en"))
        list.add(Language("Danish", "Dansk", "da"))
        list.add(Language("Dutch", "Nederlands", "nl"))
        list.add(Language("French", "Français", "fr"))
        list.add(Language("German", "Deutsch", "de"))
        list.add(Language("Greek", "Ελληνικά", "el"))
        list.add(Language("Hindi", "हिंदी", "hi"))
        list.add(Language("Indonesian ", "Bahasa Indonesia", "in"))
        list.add(Language("Italian", "Italiano", "it"))
        list.add(Language("Japanese", "日本語", "ja"))
        list.add(Language("Korean", "한국어", "ko"))
        list.add(Language("Norwegian", "norsk", "nb"))
        list.add(Language("Polish", "Polski", "pl"))
        list.add(Language("Portuguese", "Português", "pt"))
        list.add(Language("Russian", "Русский", "ru"))
        list.add(Language("Spanish", "Español", "es"))
        list.add(Language("Swedish", "Svenska", "sv"))
        list.add(Language("Thai", "ภาษาไทย", "th"))
        list.add(Language("Turkish", "Türkçe", "tr"))
        list.add(Language("Vietnamese", "Tiếng Việt", "vi"))

        return list
    }




}