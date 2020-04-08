package com.us.singledigits.myapartment.ui.devices

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.us.singledigits.myapartment.commons.utils.StaticConstants
import com.us.singledigits.myapartment.data.models.*
import com.us.singledigits.myapartment.data.network.api.MduApi
import com.us.singledigits.myapartment.data.network.responses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DevicesViewModel : ViewModel() {

//-------- get people ---------//
    var peopleItems: MutableLiveData<List<DwellingUnitResident>>? = null
    fun getPeopleItems(token:String?, residentResponse: ResidentResponse?): LiveData<List<DwellingUnitResident>>? { // token:String, residentResponse: ResidentResponse
        if (peopleItems == null) {
            peopleItems = MutableLiveData()
        }
        loadPeopleData(token,residentResponse?.links?.dwellingUnit)
        return peopleItems
    }

    private fun loadPeopleData(token:String?, url: String?) {
        MduApi().getDwellingUnit(token, url).enqueue(object :
            Callback<DwellingUnitResponse> {
            override fun onResponse(call: Call<DwellingUnitResponse>, response: Response<DwellingUnitResponse>) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getUnit API successfully")

                    val links = response.body()?.links as Links
                    val url = StaticConstants.apiBaseUrl.dropLast(1) + links.residents
                    MduApi().getUnitResidentsByUrl(token, url).enqueue(object:Callback<DwellingUnitResidentsResponse> {
                        override fun onResponse(call: Call<DwellingUnitResidentsResponse>, response: Response<DwellingUnitResidentsResponse>) {
                            Log.d("SUCCESS_API", "Called getUnitsResidentsByUrl API successfully")
                            peopleItems?.value = response.body()?.data
                            Log.d("RETURNED_DATA", peopleItems.toString())
                        }

                        override fun onFailure(call: Call<DwellingUnitResidentsResponse>, t: Throwable) {
                            Log.d("FAILED_API", "Failed to call getUnitsResidentsByUrl API, Error: " + t.message)
                        }
                    })
                }
            }

            override fun onFailure(call: Call<DwellingUnitResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getUnit API, Error: " + t.message)
            }
        })
    }

//-------- get devices details ---------//
    var myDevicesItems:  MutableLiveData<List<Device>>? = null
    var allDevicesItems:  MutableLiveData<List<Device>>? = null
    var myDevicesItemsCount:  MutableLiveData<Int>? = null
    var allDevicesItemsCount:  MutableLiveData<Int>? = null

    fun getMyDevicesItems(token:String?, residentResponse: ResidentResponse?): LiveData<List<Device>>? {
        if (myDevicesItems == null) {
            myDevicesItems = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return myDevicesItems
    }

    fun getAllDevicesItems(token:String?, residentResponse: ResidentResponse?): LiveData<List<Device>>? {
        if (allDevicesItems == null) {
            allDevicesItems = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return allDevicesItems
    }

    fun getMyDevicesItemsCount(token:String?, residentResponse: ResidentResponse?): LiveData<Int>? {
        if (myDevicesItemsCount == null) {
            myDevicesItemsCount = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return myDevicesItemsCount
    }

    fun getAllDevicesItemsCount(token:String?, residentResponse: ResidentResponse?): LiveData<Int>? {
        if (allDevicesItemsCount == null) {
            allDevicesItemsCount = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return allDevicesItemsCount
    }

    private fun loadDevicesData(token:String?, url: String?) {
        MduApi().getResidentDevices(token, url).enqueue(object:Callback<ResidentDevicesResponse> {
            override fun onResponse(call: Call<ResidentDevicesResponse>, response: Response<ResidentDevicesResponse>) {
                Log.d("SUCCESS_API", "Called getResidentDevices API successfully")
                if(response.isSuccessful) {
                    val devices: ResidentDevices? = response.body()?.data
                    if (devices != null) {

                        devices.personalDevices.forEach {
                            it.isPersonal = true
                        }

                        val allDevices = ArrayList<Device>(devices.personalDevices)
                        myDevicesItems?.value = allDevices // My devices only
                        myDevicesItemsCount?.value = allDevices.size

                        allDevices.addAll(devices.otherDevices) // add other devices
                        allDevicesItems?.value = allDevices
                        allDevicesItemsCount?.value = allDevices.size
                    }
                } else {
                    if(response.code() == 401) {
                        Log.d("FAILED_API", "Called getResidentDevices API Error 401, Unauthorized")
                        // TODO: go to the login page
                    }
                }
            }

            override fun onFailure(call: Call<ResidentDevicesResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getResidentDevices API, Error: " + t.message)
            }
        })
    }

//------- For public wifi page (currently removed) ------//
//    var site: MutableLiveData<Site>? = null
//
//    fun getSite(): LiveData<Site>? {
//        if (site == null) {
//            site = MutableLiveData()
//        }
//        loadSiteData(TEMP_ID)
//        return site
//    }
//
//    private fun loadSiteData(id: String) {
//        MenuApi().getSite(id).enqueue(object :
//            Callback<SiteResponse> {
//            override fun onResponse(call: Call<SiteResponse>, response: Response<SiteResponse>) {
//                if (response.isSuccessful) {
//                    site?.value = response.body()?.site
//                    Log.d("SUCCESS_API", "Called getSite API successfully")
//                }
//            }
//            override fun onFailure(call: Call<SiteResponse>, t: Throwable) {
//                Log.d("FAILED_API", "Failed to call getSite API, Error: " + t.message)
//            }
//        })
//    }


}