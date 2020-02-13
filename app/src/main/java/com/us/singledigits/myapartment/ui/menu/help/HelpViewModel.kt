package com.us.singledigits.myapartment.ui.menu.help

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.us.singledigits.myapartment.data.models.*
import com.us.singledigits.myapartment.data.network.api.MenuApi
import com.us.singledigits.myapartment.data.network.responses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HelpViewModel : ViewModel() {
    var helpTopicsItems: MutableLiveData<List<HelpTopicListItem>>? = null

    fun getHelpTopicItems(): LiveData<List<HelpTopicListItem>>? {
        if (helpTopicsItems == null) {
            helpTopicsItems = MutableLiveData()
        }
        loadHelpTopicsData()
        return helpTopicsItems
    }

    private fun loadHelpTopicsData() {
        MenuApi().getHelpTopics().enqueue(object :
            Callback<HelpTopicsResponse> {
            override fun onFailure(call: Call<HelpTopicsResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getHelpTopics API, Error: " + t.message)
            }

            override fun onResponse(call: Call<HelpTopicsResponse>, response: Response<HelpTopicsResponse>) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getHelpTopics API successfully")
                    helpTopicsItems?.value = response.body()?.helpTopicItems
                }
            }
        })
    }


}