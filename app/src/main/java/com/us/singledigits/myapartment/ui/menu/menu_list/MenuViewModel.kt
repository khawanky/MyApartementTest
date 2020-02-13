package com.us.singledigits.myapartment.ui.menu.menu_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.us.singledigits.myapartment.data.models.DwellingUnit
import com.us.singledigits.myapartment.data.models.DwellingUnitRoom
import com.us.singledigits.myapartment.data.models.Resident
import com.us.singledigits.myapartment.data.models.Site
import com.us.singledigits.myapartment.data.network.api.MenuApi
import com.us.singledigits.myapartment.data.network.responses.DwellingUnitResponse
import com.us.singledigits.myapartment.data.network.responses.DwellingUnitRoomResponse
import com.us.singledigits.myapartment.data.network.responses.ResidentResponse
import com.us.singledigits.myapartment.data.network.responses.SiteResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel : ViewModel() {
    var resident: MutableLiveData<Resident>? = null
    var unit: MutableLiveData<DwellingUnit>? = null
    var room: MutableLiveData<DwellingUnitRoom>? = null
    var site: MutableLiveData<Site>? = null

    // TODO: Handel real dynamic Id
    private val TEMP_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6"

    fun getResident(): LiveData<Resident>? {
        if (resident == null) {
            resident = MutableLiveData()
        }
        loadResidentData(TEMP_ID)
        return resident
    }

    fun getUnit(): LiveData<DwellingUnit>? {
        if (unit == null) {
            unit = MutableLiveData()
        }
        loadUnitData(TEMP_ID)
        return unit
    }

    fun getRoom(): LiveData<DwellingUnitRoom>? {
        if (room == null) {
            room = MutableLiveData()
        }
        loadRoomData(TEMP_ID)
        return room
    }

    fun getSite(): LiveData<Site>? {
        if (site == null) {
            site = MutableLiveData()
        }
        loadSiteData(TEMP_ID)
        return site
    }

    private fun loadResidentData(id: String) {
        MenuApi().getResident(id).enqueue(object :
            Callback<ResidentResponse> {
            override fun onFailure(call: Call<ResidentResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getResident API, Error: " + t.message)
            }

            override fun onResponse(
                call: Call<ResidentResponse>,
                response: Response<ResidentResponse>
            ) {
                if (response.isSuccessful) {
                    resident?.value = response.body()?.resident
                    Log.d("SUCCESS_API", "Called getResident API successfully")
                }
            }
        })
    }

    private fun loadUnitData(id: String) {
        MenuApi().getUnit(id).enqueue(object :
            Callback<DwellingUnitResponse> {
            override fun onFailure(call: Call<DwellingUnitResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getUnit API, Error: " + t.message)
            }

            override fun onResponse(
                call: Call<DwellingUnitResponse>,
                response: Response<DwellingUnitResponse>
            ) {
                if (response.isSuccessful) {
                    unit?.value = response.body()?.unit
                    Log.d("SUCCESS_API", "Called getUnit API successfully")
                }
            }
        })
    }

    private fun loadRoomData(id: String) {
        MenuApi().getRoom(id).enqueue(object :
            Callback<DwellingUnitRoomResponse> {
            override fun onFailure(call: Call<DwellingUnitRoomResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getRoom API, Error: " + t.message)
            }

            override fun onResponse(
                call: Call<DwellingUnitRoomResponse>,
                response: Response<DwellingUnitRoomResponse>
            ) {
                if (response.isSuccessful) {
                    room?.value = response.body()?.room
                    Log.d("SUCCESS_API", "Called getRoom API successfully")
                }
            }
        })
    }

    private fun loadSiteData(id: String) {
        MenuApi().getSite(id).enqueue(object :
            Callback<SiteResponse> {
            override fun onFailure(call: Call<SiteResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getSite API, Error: " + t.message)
            }

            override fun onResponse(call: Call<SiteResponse>, response: Response<SiteResponse>) {
                if (response.isSuccessful) {
                    site?.value = response.body()?.site
                    Log.d("SUCCESS_API", "Called getSite API successfully")
                }
            }
        })
    }

}