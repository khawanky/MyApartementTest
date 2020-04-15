package com.us.singledigits.myapartment.ui.menu.help

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.us.singledigits.myapartment.commons.ui.BaseViewModel
import com.us.singledigits.myapartment.commons.utils.StaticConstants
import com.us.singledigits.myapartment.data.models.*
import com.us.singledigits.myapartment.data.network.api.MduApi
import com.us.singledigits.myapartment.data.network.responses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HelpViewModel : BaseViewModel() {
    var helpTopicsItems: MutableLiveData<List<HelpTopicListItem>>? = null

    fun getHelpTopicItems(token:String?, residentResponse: ResidentResponse?): LiveData<List<HelpTopicListItem>>? {
        if (helpTopicsItems == null) {
            helpTopicsItems = MutableLiveData()
        }
        loadHelpTopicsData(token, residentResponse?.links?.site)
        return helpTopicsItems
    }

    private fun loadHelpTopicsData(token:String?, url: String?) {
        MduApi().getSite(token, url).enqueue(object : Callback<SiteResponse> {
            override fun onResponse(call: Call<SiteResponse>, response: Response<SiteResponse>) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getSite API successfully")
                    val links = response.body()?.links as ResidentLinks
                    val url = StaticConstants.apiBaseUrl.dropLast(1) + links.helpTopics

                    MduApi().getHelpTopics(token, url).enqueue(object : Callback<HelpTopicsResponse> {
                        override fun onResponse(call: Call<HelpTopicsResponse>, response: Response<HelpTopicsResponse>) {
                            if (response.isSuccessful) {
                                Log.d("SUCCESS_API", "Called getHelpTopics API successfully")
                                helpTopicsItems?.value = response.body()?.helpTopicItems
                            } else {
                                Log.d("NOT_SUCCESS_API", "Call getHelpTopics API, return with code= " + response.code())
                                handleErrorCodes(response.code(), null)
                            }
                        }
                        override fun onFailure(call: Call<HelpTopicsResponse>, t: Throwable) {
                            Log.d("FAILED_API", "Failed to call getHelpTopics API, Error: " + t.message)
                            handleErrorCodes(20, "Error calling getHelpTopics")
                        }
                    })
                }
            }
            override fun onFailure(call: Call<SiteResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getSite API, Error: " + t.message)
                handleErrorCodes(20, "Error calling getSite")
            }
        })
    }


}