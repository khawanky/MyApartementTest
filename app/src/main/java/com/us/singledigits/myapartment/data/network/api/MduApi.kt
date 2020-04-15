package com.us.singledigits.myapartment.data.network.api

import com.us.singledigits.myapartment.commons.utils.StaticConstants
import com.us.singledigits.myapartment.data.models.DeviceRequestBody
import com.us.singledigits.myapartment.data.models.Login
import com.us.singledigits.myapartment.data.models.NotificationAckRequestBody
import com.us.singledigits.myapartment.data.network.responses.*
import com.us.singledigits.myapartment.ui.login.data.model.LoggedInUser
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MduApi {
    // Login
    @POST("mdu/authentication")
    fun login(@Body userCredentials:Login) : Call<LoggedInUser>

    // Devices
    @GET
    fun getResidentDevices(@Header("x-bap-auth") token:String?, @Url url:String?) : Call<ResidentDevicesResponse>

    @GET
    fun getDwellingUnitDevices(@Header("x-bap-auth") token:String?, @Url url:String?) : Call<DwellingUnitDeviceResponse>

    @POST("mdu/residentDevices")
    fun addDevice(@Header("x-bap-auth") token:String?, @Body requestBody:DeviceRequestBody) : Call<Void>

    @PUT("mdu/residentDevices/{id}")
    fun updateDevice(@Header("x-bap-auth") token:String?, @Path(value = "id") deviceId:Int?, @Body requestBody:DeviceRequestBody) : Call<Void>

    @DELETE("mdu/residentDevices/{id}")
    fun deleteDevice(@Header("x-bap-auth") token:String?, @Path(value = "id") deviceId:Int?) : Call<Void>

    // Units
    @GET
    fun getDwellingUnit(@Header("x-bap-auth") token:String?, @Url url:String?) : Call<DwellingUnitResponse>

    @GET
    fun getUnitResidentsByUrl(@Header("x-bap-auth") token:String?, @Url url:String) : Call<DwellingUnitResidentsResponse>

    // Sites
    @GET
    fun getSite(@Header("x-bap-auth") token:String?, @Url url:String?) : Call<SiteResponse>

    @GET
    fun getSiteServices(@Header("x-bap-auth") token:String?, @Url url:String?) : Call<SiteServicesResponse>

    @GET
    fun getHelpTopics(@Header("x-bap-auth") token:String?, @Url url:String?) : Call<HelpTopicsResponse>

    @GET
    fun getResidentNotificationsByUrl(@Header("x-bap-auth") token:String?, @Url url:String?) : Call<NotificationsResponse>

    @PATCH("mdu/notifications/{id}")
    fun sendNotificationAcknowledgement(@Header("x-bap-auth") token:String?, @Path(value = "id") id:Int, @Body payload: NotificationAckRequestBody?) : Call<Void>

    companion object {
        operator fun invoke(): MduApi {
            return Retrofit.Builder()
                .baseUrl(StaticConstants.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient())
                .build().create(MduApi::class.java)
        }
    }
}