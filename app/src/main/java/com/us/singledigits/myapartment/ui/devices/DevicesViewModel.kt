package com.us.singledigits.myapartment.ui.devices

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

class DevicesViewModel : ViewModel() {
    // TODO: Handel real dynamic Id
    private val TEMP_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    val baseUrl="https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0"
    var peopleItems: MutableLiveData<List<DwellingUnitResident>>? = null
    var myDevicesItems:  MutableLiveData<List<Device>>? = null
    var allDevicesItems:  MutableLiveData<List<Device>>? = null
    var site: MutableLiveData<Site>? = null
    var unit: MutableLiveData<DwellingUnitAttributes>? = null


    var myDevicesItemsCount:  MutableLiveData<Int>? = null
    var allDevicesItemsCount:  MutableLiveData<Int>? = null


    fun getPeopleItems(): LiveData<List<DwellingUnitResident>>? {
        if (peopleItems == null) {
            peopleItems = MutableLiveData()
        }
        loadPeopleData(TEMP_ID)
        return peopleItems
    }


    fun getMyDevicesItems(): LiveData<List<Device>>? {
        if (myDevicesItems == null) {
            myDevicesItems = MutableLiveData()
        }
        loadDevicesData(TEMP_ID)
        return myDevicesItems
    }

    fun getAllDevicesItems(): LiveData<List<Device>>? {
        if (allDevicesItems == null) {
            allDevicesItems = MutableLiveData()
        }
        loadDevicesData(TEMP_ID)
        return allDevicesItems
    }

    fun getMyDevicesItemsCount(): LiveData<Int>? {
        if (myDevicesItemsCount == null) {
            myDevicesItemsCount = MutableLiveData()
        }
        return myDevicesItemsCount
    }

    fun getAllDevicesItemsCount(): LiveData<Int>? {
        if (allDevicesItemsCount == null) {
            allDevicesItemsCount = MutableLiveData()
        }
        loadDevicesData(TEMP_ID)
        return allDevicesItemsCount
    }

    private fun loadDevicesData(id: String) {
        DevicesApi().getResidentDevices(id).enqueue(object:Callback<ResidentDevicesResponse> {
            override fun onResponse(call: Call<ResidentDevicesResponse>, response: Response<ResidentDevicesResponse>) {
                Log.d("SUCCESS_API", "Called getResidentDevices API successfully")

                val devices: List<ResidentDevices>? = response.body()?.data

                if(devices != null) {

                    devices[0].personalDevices.forEach{
                        it.isPersonal = true
                    }

                    val allDevices = ArrayList<Device>(devices[0].personalDevices)
                    myDevicesItems?.value = allDevices // My devices only
                    myDevicesItemsCount?.value = allDevices.size

                    allDevices.addAll(devices[0].otherDevices) // add other devices
                    allDevicesItems?.value = allDevices
                    allDevicesItemsCount?.value = allDevices.size
                }
            }

            override fun onFailure(call: Call<ResidentDevicesResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getResidentDevices API, Error: " + t.message)
            }
        })
    }

    private fun loadPeopleData(id: String) {
        MenuApi().getUnit(id).enqueue(object :
            Callback<DwellingUnitResponse> {
            override fun onResponse(call: Call<DwellingUnitResponse>, response: Response<DwellingUnitResponse>) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getUnit API successfully")

                    val links = response.body()?.links as Links
                    val url = baseUrl+links.residents
                    DevicesApi().getUnitsResidentsByUrl(url).enqueue(object:Callback<DwellingUnitResidentResponse> {
                        override fun onResponse(call: Call<DwellingUnitResidentResponse>, response: Response<DwellingUnitResidentResponse>) {
                            Log.d("SUCCESS_API", "Called getUnitsResidentsByUrl API successfully")
                            peopleItems?.value = response.body()?.data
                            Log.d("RETURNED_DATA", peopleItems.toString())
                        }

                        override fun onFailure(call: Call<DwellingUnitResidentResponse>, t: Throwable) {
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

    fun getSite(): LiveData<Site>? {
        if (site == null) {
            site = MutableLiveData()
        }
        loadSiteData(TEMP_ID)
        return site
    }

    fun getUnit(): LiveData<DwellingUnitAttributes>? {
        if (unit == null) {
            unit = MutableLiveData()
        }
        loadUnitData(TEMP_ID)
        return unit
    }

    private fun loadSiteData(id: String) {
        MenuApi().getSite(id).enqueue(object :
            Callback<SiteResponse> {
            override fun onResponse(call: Call<SiteResponse>, response: Response<SiteResponse>) {
                if (response.isSuccessful) {
                    site?.value = response.body()?.site
                    Log.d("SUCCESS_API", "Called getSite API successfully")
                }
            }
            override fun onFailure(call: Call<SiteResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getSite API, Error: " + t.message)
            }
        })
    }

    private fun loadUnitData(id: String) {
        MenuApi().getUnit(id).enqueue(object :
            Callback<DwellingUnitResponse> {
            override fun onResponse(call: Call<DwellingUnitResponse>, response: Response<DwellingUnitResponse>) {
                if (response.isSuccessful) {
                    unit?.value = response.body()?.unit
                    Log.d("SUCCESS_API", "Called getUnit API successfully")
                }
            }
            override fun onFailure(call: Call<DwellingUnitResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getUnit API, Error: " + t.message)
            }
        })
    }


}