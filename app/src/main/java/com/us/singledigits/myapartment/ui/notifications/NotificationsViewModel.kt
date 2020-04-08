package com.us.singledigits.myapartment.ui.notifications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.us.singledigits.myapartment.data.models.*
import com.us.singledigits.myapartment.data.network.api.MduApi
import com.us.singledigits.myapartment.data.network.responses.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsViewModel : ViewModel() {
    // Check if have new notification
    var hasUnreadNotifications: MutableLiveData<Boolean> ? = null
    fun hasUnreadNotifications(token:String?, residentResponse: ResidentResponse?): LiveData<Boolean>? {
        if (hasUnreadNotifications == null) {
            hasUnreadNotifications = MutableLiveData()
        }
        checkIfHasUnreadNotifications(token, residentResponse)
        return hasUnreadNotifications
    }

    private fun checkIfHasUnreadNotifications(token:String?, residentResponse: ResidentResponse?) {
        MduApi().getResidentNotificationsByUrl(token, residentResponse?.links?.notifications).enqueue(object:Callback<NotificationsResponse> {
            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                Log.d("SUCCESS_API", "Called getResidentNotificationsByUrl API successfully")
                hasUnreadNotifications?.value = response.body()?.hasUnreadNotifications
                Log.d("RETURNED_DATA","hasUnreadNotifications="+ hasUnreadNotifications.toString())
            }
            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getResidentNotificationsByUrl API, Error: " + t.message)
            }
        })
    }

    // get all notifications
    var notificationsItems: MutableLiveData<List<Notification>>? = null

    fun getNotificationsItems(token:String?, residentResponse: ResidentResponse?): LiveData<List<Notification>>? {
        if (notificationsItems == null) {
            notificationsItems = MutableLiveData()
        }
        loadNotificationsData(token, residentResponse)
        return notificationsItems
    }

    private fun loadNotificationsData(token:String?, residentResponse: ResidentResponse?) {
        MduApi().getResidentNotificationsByUrl(token, residentResponse?.links?.notifications).enqueue(object:Callback<NotificationsResponse> {
            override fun onResponse(call: Call<NotificationsResponse>, response: Response<NotificationsResponse>) {
                Log.d("SUCCESS_API", "Called getResidentNotificationsByUrl API successfully")
                notificationsItems?.value = response.body()?.data
                Log.d("RETURNED_DATA", notificationsItems.toString())
            }
            override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call getResidentNotificationsByUrl API, Error: " + t.message)
            }
        })
    }

    // send acknowledgement
    fun sendNotificationAcknowledgement(token:String?, notificationId:Int, payload:String) {
        MduApi().sendNotificationAcknowledgement(token,notificationId, payload).enqueue(
            object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if(response.isSuccessful) {
                        Log.d(
                            "SUCCESS_API", "Called sendNotificationAcknowledgement API successfully"
                        )
                    } else {
                        Log.d("NOT_SUCCESS_API", "Call sendNotificationAcknowledgement API, return with code=: " + response.code())
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("FAILED_API", "Failed to call sendNotificationAcknowledgement API, Error: " + t.message)
                }

            }
        )
    }


}