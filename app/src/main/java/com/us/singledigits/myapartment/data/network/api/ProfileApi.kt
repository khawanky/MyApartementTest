package com.us.singledigits.myapartment.data.network.api

import com.us.singledigits.myapartment.data.network.responses.DwellingUnitResponse
import com.us.singledigits.myapartment.data.network.responses.DwellingUnitRoomResponse
import com.us.singledigits.myapartment.data.network.responses.ResidentResponse
import com.us.singledigits.myapartment.data.network.responses.SiteResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApi {

    //https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/mdu/residents/3fa85f64-5717-4562-b3fc-2c963f66afa6


    @GET("mdu/residents/{id}")
    fun getResident(@Path(value = "id") id:String) : Call<ResidentResponse>

    @GET("mdu/dwelling-units/{id}")
    fun getUnit(@Path(value = "id") id:String) : Call<DwellingUnitResponse>

    @GET("mdu/dwelling-unit-rooms/{id}")
    fun getRoom(@Path(value = "id") id:String) : Call<DwellingUnitRoomResponse>

    @GET("mdu/sites/{id}")
    fun getSite(@Path(value = "id") id:String) : Call<SiteResponse>

    companion object {
        operator fun invoke(): ProfileApi {
            val baseUrl="https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/"
            return Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ProfileApi::class.java)
        }
    }
}