package com.us.singledigits.myapartment.commons.utils

import android.content.Context

class StaticConstants {
    private var sharedPreferencePreferences: SharedPreferencesConfig? = null
//    val apiBaseUrl = "https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/"
    val apiBaseUrl = "http://mdu-api.qa.mdu-apps.aws.opennetworkexchange.net/"

    fun getSharedPreferencesConfig(context: Context?): SharedPreferencesConfig? {
        if (sharedPreferencePreferences == null) {
            sharedPreferencePreferences = SharedPreferencesConfig(context)
        }
        return sharedPreferencePreferences
    }
}