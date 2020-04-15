package com.us.singledigits.myapartment.ui.devices

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


class DevicesViewModel : BaseViewModel() {
    // Some needed data to send add and update device apis, to be observed in the devices tabs fragments
    var userGroupId: MutableLiveData<String?>? = MutableLiveData()

    fun getUserGroupId(): LiveData<String?>? = userGroupId

    var accountId: MutableLiveData<String?>? = MutableLiveData()
    fun getAccountId(): LiveData<String?>? = accountId

    var deviceAPIStatus:MutableLiveData<Int?>? = MutableLiveData() // 1=add, 2=update, 3=delete

    //-------- get people ---------//
    var peopleItems: MutableLiveData<List<DwellingUnitResident>>? = null
    var dwellingUnitAttributes: MutableLiveData<DwellingUnitAttributes>? = null

    fun getPeopleItems(token: String?, residentResponse: ResidentResponse?): LiveData<List<DwellingUnitResident>>? {
        if (peopleItems == null) {
            peopleItems = MutableLiveData()
        }
        loadPeopleData(token, residentResponse?.links?.dwellingUnit)
        return peopleItems
    }

    fun getDwellingUnitAttributes(): LiveData<DwellingUnitAttributes>? {
        if (dwellingUnitAttributes == null) {
            dwellingUnitAttributes = MutableLiveData()
        }
        return dwellingUnitAttributes
    }

    private fun loadPeopleData(token: String?, url: String?) {
        MduApi().getDwellingUnit(token, url).enqueue(object :
            Callback<DwellingUnitResponse> {
            override fun onResponse(
                call: Call<DwellingUnitResponse>,
                response: Response<DwellingUnitResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getDwellingUnit API successfully")
                    dwellingUnitAttributes?.value = response.body()?.unit // to use in the devices page for private wifi
                    userGroupId?.value = response.body()?.unit?.userGroupID // to use in devices tabs Fragments

                    val links = response.body()?.links as Links
                    val siteLinkParts = links.site.split("/")
                    accountId?.value = siteLinkParts[siteLinkParts.size - 1] // get the site Id from the link

                    val url = StaticConstants.apiBaseUrl.dropLast(1) + links.residents
                    MduApi().getUnitResidentsByUrl(token, url).enqueue(object : Callback<DwellingUnitResidentsResponse> {
                        override fun onResponse(call: Call<DwellingUnitResidentsResponse>, response: Response<DwellingUnitResidentsResponse>) {
                            if(response.isSuccessful) {
                                Log.d("SUCCESS_API","Called getUnitsResidentsByUrl API successfully")
                                peopleItems?.value = response.body()?.data  // to use in devices page to load people list
                            } else {
                                Log.d("NOT_SUCCESS_API", "Call getUnitsResidentsByUrl API, return with code= " + response.code())
                                handleErrorCodes(response.code(), null)
                            }
                        }

                        override fun onFailure(call: Call<DwellingUnitResidentsResponse>, t: Throwable) {
                            Log.d("FAILED_API","Failed to call getUnitsResidentsByUrl API, Error: " + t.message)
                            handleErrorCodes(20, "Error calling getUnitsResidentsByUrl")
                        }
                    })
                } else {
                    Log.d("NOT_SUCCESS_API", "Call getDwellingUnit API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }

            override fun onFailure(call: Call<DwellingUnitResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getDwellingUnit API, Error: " + t.message)
                handleErrorCodes(20, "Error calling getDwellingUnit")
            }
        })
    }

    //-------- get devices details ---------//
    var myDevicesItems: MutableLiveData<List<Device>>? = null
    var allDevicesItems: MutableLiveData<List<Device>>? = null
    var myDevicesItemsCount: MutableLiveData<Int>? = null
    var allDevicesItemsCount: MutableLiveData<Int>? = null

    fun getMyDevicesItems(
        token: String?,
        residentResponse: ResidentResponse?
    ): LiveData<List<Device>>? {
        if (myDevicesItems == null) {
            myDevicesItems = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return myDevicesItems
    }

    fun getAllDevicesItems(
        token: String?,
        residentResponse: ResidentResponse?
    ): LiveData<List<Device>>? {
        if (allDevicesItems == null) {
            allDevicesItems = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return allDevicesItems
    }

    fun getMyDevicesItemsCount(
        token: String?,
        residentResponse: ResidentResponse?
    ): LiveData<Int>? {
        if (myDevicesItemsCount == null) {
            myDevicesItemsCount = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return myDevicesItemsCount
    }

    fun getAllDevicesItemsCount(
        token: String?,
        residentResponse: ResidentResponse?
    ): LiveData<Int>? {
        if (allDevicesItemsCount == null) {
            allDevicesItemsCount = MutableLiveData()
        }
        loadDevicesData(token, residentResponse?.links?.residentDevices)
        return allDevicesItemsCount
    }

    private fun loadDevicesData(token: String?, url: String?) {
        MduApi().getResidentDevices(token, url).enqueue(object : Callback<ResidentDevicesResponse> {
            override fun onResponse(
                call: Call<ResidentDevicesResponse>,
                response: Response<ResidentDevicesResponse>
            ) {
                Log.d("SUCCESS_API", "Called getResidentDevices API successfully")
                if (response.isSuccessful) {
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
                    Log.d("NOT_SUCCESS_API", "Call getResidentDevices API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<ResidentDevicesResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getResidentDevices API, Error: " + t.message)
                handleErrorCodes(20, "Error calling getResidentDevices")
            }
        })
    }

    // Devices Crud
    fun addDevice(token: String?, requestBody: DeviceRequestBody) {
        MduApi().addDevice(token, requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called addDevice API successfully")
                    deviceAPIStatus?.value = 1
                } else {
                    Log.d("NOT_SUCCESS_API", "Call addDevice API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call addDevice API, Error: " + t.message)
                handleErrorCodes(20, "Error calling addDevice")
            }
        })
    }

    fun updateDevice(token: String?, deviceId:Int?, requestBody: DeviceRequestBody) {
        MduApi().updateDevice(token, deviceId, requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called updateDevice API successfully")
                    deviceAPIStatus?.value = 2
                } else {
                    Log.d("NOT_SUCCESS_API", "Call updateDevice API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call updateDevice API, Error: " + t.message)
                handleErrorCodes(20, "Error calling updateDevice")
            }
        })
    }

    fun deleteDevice(token: String?, deviceId:Int?) {
        MduApi().deleteDevice(token, deviceId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called deleteDevice API successfully")
                    deviceAPIStatus?.value = 2
                } else {
                    Log.d("NOT_SUCCESS_API", "Call deleteDevice API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call deleteDevice API, Error: " + t.message)
                handleErrorCodes(20, "Error calling deleteDevice")
            }
        })
    }

////------- For public wifi page (currently removed), and some needed data for devices requests ------//
//    var siteAttributes: MutableLiveData<SiteAttributes>? = null
//    fun getSite(token:String?, residentResponse: ResidentResponse?): LiveData<SiteAttributes>? {
//        if (siteAttributes == null) {
//            siteAttributes = MutableLiveData()
//        }
//        loadSiteData(token, residentResponse?.links?.residentDevices)
//        return siteAttributes
//    }
//    private fun loadSiteData(token:String?, url: String?) {
//        MduApi().getSite(token,url).enqueue(object : Callback<SiteResponse> {
//            override fun onResponse(call: Call<SiteResponse>, response: Response<SiteResponse>) {
//                if (response.isSuccessful) {
//                    Log.d("SUCCESS_API", "Called getSite API successfully")
//                    siteAttributes?.value = response.body()?.siteAttributes
//                }
//            }
//            override fun onFailure(call: Call<SiteResponse>, t: Throwable) {
//                Log.d("FAILED_API", "Failed to call getSite API, Error: " + t.message)
//            }
//        })
//    }

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