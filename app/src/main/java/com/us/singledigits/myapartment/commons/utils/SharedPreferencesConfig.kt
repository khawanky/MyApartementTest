package com.us.singledigits.myapartment.commons.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesConfig (val context: Context?) {
    private val sharedPrefsName = "myApartmentPreferences"
    private val token = "token"
    private val resident = "resident"

    val sharedPrefs: SharedPreferences = context!!.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)

    //------ token ------
    fun setToken(token: String) {
        sharedPrefs.edit().putString(this.token, token).commit()
    }

    fun getToken(): String? {
        return sharedPrefs.getString(token, "NA")
    }

    //------ resident ------
    fun setResident(residentString: String?) {
        sharedPrefs.edit().putString(resident, residentString).commit()
    }

    fun getResident(): String? {
        return sharedPrefs.getString(resident, "NA")
    }

    //------- invalidate methods -----
    fun clearSharedPreference() {
        sharedPrefs.edit().clear().commit()
    }

    fun removeValue(keyName: String) {
        sharedPrefs.edit().remove(keyName).commit()
    }

}