package com.us.singledigits.myapartment.data.network.api

import com.us.singledigits.myapartment.data.network.responses.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface MenuApi {

    //https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/mdu/residents/3fa85f64-5717-4562-b3fc-2c963f66afa6

    @GET("mdu/residents/{id}")
    fun getResident(@Path(value = "id") id:String) : Call<ResidentResponse>

    @GET("mdu/dwelling-units/{id}")
    fun getUnit(@Path(value = "id") id:String) : Call<DwellingUnitResponse>

    @GET("mdu/dwelling-unit-rooms/{id}")
    fun getRoom(@Path(value = "id") id:String) : Call<DwellingUnitRoomResponse>

    @GET("mdu/sites/{id}")
    fun getSite(@Path(value = "id") id:String) : Call<SiteResponse>

    @GET("mdu/help-topics")
    fun getHelpTopics() : Call<HelpTopicsResponse>

    companion object {
        operator fun invoke(): MenuApi {
            val baseUrl="https://virtserver.swaggerhub.com/Single-Digits/MDU/1.0/"
            return Retrofit.Builder().baseUrl(baseUrl)
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MenuApi::class.java)
        }
    }
}