package com.us.singledigits.myapartment.data.repositories

import android.util.Log
import com.us.singledigits.myapartment.data.models.Resident
import com.us.singledigits.myapartment.data.network.api.ProfileApi
import com.us.singledigits.myapartment.data.network.responses.ResidentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository (private val api:ProfileApi) {

    fun getResident(id:String): Resident {

        var resident = Resident("","","ABC","EDF",
            "","", "", true)

        api.getResident(id).enqueue(object : Callback<ResidentResponse>{
            override fun onFailure(call: Call<ResidentResponse>, t: Throwable) {
                Log.d("FAILURE_API","Failed to call API")
            }

            override fun onResponse(call: Call<ResidentResponse>, response: Response<ResidentResponse>) {
                if(response.isSuccessful){
                    resident = response.body()!!.resident
                    Log.d("CALLING","Called successfully")
                }
            }
        })
        return resident
    }
}