package com.us.singledigits.myapartment.commons.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.us.singledigits.myapartment.home.HomePageActivity
import com.us.singledigits.myapartment.login.ui.login.LoginActivity
import java.util.function.Predicate


class CommonUtils {
    fun setSharedData(c: Context, key: String?, data: String?) {
        val settings: SharedPreferences =
            c.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE)
        val editor = settings.edit()
        editor.putString(key, data) //putString or putInt
        editor.apply() // Apply the edits!
    }

    fun getSharedData(c: Context, getKey: String?): String? {
        val settings = c.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE)
        return settings.getString(getKey, null)
    }

    fun goToHomepage(activity: Activity) {
        Log.i("GoTo_tas", "homePage")
        val homePageIntent = Intent(activity, HomePageActivity::class.java)
        homePageIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(homePageIntent)
    }

    fun goToLogin(activity: Activity) {
        Log.i("GoTo_tas", "homePage")
        val loginActivityIntent = Intent(activity, LoginActivity::class.java)
        loginActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(loginActivityIntent)
    }


    fun Context.toast (message:String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun ProgressBar.show (){
        visibility = View.VISIBLE
    }


    fun ProgressBar.hide (){
        visibility = View.GONE
    }

}