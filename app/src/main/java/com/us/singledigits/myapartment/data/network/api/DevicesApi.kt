package com.us.singledigits.myapartment.data.network.api

import com.us.singledigits.myapartment.data.network.responses.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface DevicesApi {

    //https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/mdu/sites/1d93f45e-2e39-4e6e-8c63-0a0e07e44158/services
    //https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/mdu/dwelling-units/3fa85f64-5717-4562-b3fc-2c963f66afa6/devices

    @GET("mdu/sites/{id}/services")
    fun getService(@Path(value = "id") id:String) : Call<ServiceResponse>

    @GET("mdu/dwelling-units/{id}/devices")
    fun getDevices(@Path(value = "id") id:String) : Call<DwellingUnitDeviceResponse>

    @GET("mdu/dwelling-units/{id}/residents")
    fun getUnitsResidents(@Path(value = "id") id:String) : Call<DwellingUnitResidentResponse>

    @GET
    fun getUnitsResidentsByUrl(@Url url:String) : Call<DwellingUnitResidentResponse>

    @GET("mdu/residents/{id}/devices")
    fun getResidentDevices(@Path(value = "id") id:String) : Call<ResidentDevicesResponse>


    companion object {
        operator fun invoke(): DevicesApi {
            val baseUrl="https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/"
            return Retrofit.Builder().baseUrl(baseUrl)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(DevicesApi::class.java)
        }
    }
}