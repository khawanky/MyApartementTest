package com.us.singledigits.myapartment.ui.menu.menu_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.us.singledigits.myapartment.commons.ui.BaseViewModel
import com.us.singledigits.myapartment.data.models.DwellingUnitAttributes
import com.us.singledigits.myapartment.data.models.SiteAttributes
import com.us.singledigits.myapartment.data.network.api.MduApi
import com.us.singledigits.myapartment.data.network.responses.DwellingUnitResponse
import com.us.singledigits.myapartment.data.network.responses.ResidentResponse
import com.us.singledigits.myapartment.data.network.responses.SiteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel : BaseViewModel() {
    var siteAttributes: MutableLiveData<SiteAttributes>? = null
    var dwellingUnitAttributes: MutableLiveData<DwellingUnitAttributes>? = null

    fun getSite(token:String?, residentResponse: ResidentResponse?): LiveData<SiteAttributes>? {
        if (siteAttributes == null) {
            siteAttributes = MutableLiveData()
        }
        loadSiteData(token,residentResponse?.links?.site)
        return siteAttributes
    }

    fun getDwellingUnit(token:String?, residentResponse: ResidentResponse?): LiveData<DwellingUnitAttributes>? {
        if (dwellingUnitAttributes == null) {
            dwellingUnitAttributes = MutableLiveData()
        }
        loadDwellingUnitData(token, residentResponse?.links?.dwellingUnit)
        return dwellingUnitAttributes
    }

    private fun loadSiteData(token:String?, url: String?) {
        MduApi().getSite(token,url).enqueue(object : Callback<SiteResponse> {
            override fun onResponse(call: Call<SiteResponse>, response: Response<SiteResponse>) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getSite API successfully")
                    siteAttributes?.value = response.body()?.siteAttributes
                } else {
                    Log.d("NOT_SUCCESS_API", "Call getSite API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<SiteResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getSite API, Error: " + t.message)
                handleErrorCodes(20, "Error calling getSite")
            }
        })
    }

    private fun loadDwellingUnitData(token:String?, url: String?) {
        MduApi().getDwellingUnit(token, url).enqueue(object :
            Callback<DwellingUnitResponse> {
            override fun onResponse(call: Call<DwellingUnitResponse>, response: Response<DwellingUnitResponse>) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getUnit API successfully")
                    dwellingUnitAttributes?.value = response.body()?.unit
                } else {
                    Log.d("NOT_SUCCESS_API", "Call getUnit API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<DwellingUnitResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getUnit API, Error: " + t.message)
                handleErrorCodes(20, "Error calling getUnit")
            }
        })
    }

}