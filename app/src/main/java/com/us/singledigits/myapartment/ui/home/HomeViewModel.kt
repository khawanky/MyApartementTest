package com.us.singledigits.myapartment.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.us.singledigits.myapartment.data.models.*
import com.us.singledigits.myapartment.data.network.api.DevicesApi
import com.us.singledigits.myapartment.data.network.api.MenuApi
import com.us.singledigits.myapartment.data.network.responses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    var siteService: MutableLiveData<List<Service>>? = null
    var devices: MutableLiveData<List<DwellingUnitDevice>>? = null

    // TODO: Handel real dynamic Id
    private val TEMP_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6"


    fun getDevicesStatus(): LiveData<List<DwellingUnitDevice>>? {
        if (devices == null) {
            devices = MutableLiveData()
        }
        loadDevicesStatus(TEMP_ID)
        return devices
    }



    fun getSiteServiceData(): LiveData<List<Service>>? {
        if (siteService == null) {
            siteService = MutableLiveData()
        }
        loadServiceData(TEMP_ID)
        return siteService
    }

    private fun loadServiceData(id:String) {
        DevicesApi().getService(id).enqueue(object :
            Callback<ServiceResponse> {
            override fun onResponse(call: Call<ServiceResponse>, response: Response<ServiceResponse>) {
                if (response.isSuccessful) {
                    siteService?.value = response.body()?.data
                    Log.d("SUCCESS_API", "Called getService API successfully")
                }
            }
            override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getService API, Error: " + t.message)
            }
        })
    }

    private fun loadDevicesStatus(id:String) {
        DevicesApi().getDevices(id).enqueue(object :
            Callback<DwellingUnitDeviceResponse> {
            override fun onResponse(
                call: Call<DwellingUnitDeviceResponse>,
                response: Response<DwellingUnitDeviceResponse>
            ) {
                if (response.isSuccessful) {
                    devices?.value = response.body()?.data
                    Log.d("SUCCESS_API", "Called getService API successfully")
                }
            }

            override fun onFailure(call: Call<DwellingUnitDeviceResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getDevices API, Error: " + t.message)
            }
        })
    }


}