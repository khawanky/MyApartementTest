package com.us.singledigits.myapartment.commons.ui

import android.content.pm.ActivityInfo
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.us.singledigits.myapartment.commons.utils.StaticConstants
import com.us.singledigits.myapartment.data.network.responses.ResidentResponse

abstract class BaseActivity : AppCompatActivity () {

    var token:String? = null
    var residentModel: ResidentResponse? =  null
    val gson = Gson()
    fun loadSharedPreferenceData() {
        token = StaticConstants().getSharedPreferencesConfig(this)?.getToken()
        val jsonResident = StaticConstants().getSharedPreferencesConfig(this)?.getResident()
        residentModel = gson.fromJson(jsonResident, ResidentResponse::class.java)
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}