package com.us.singledigits.myapartment.data.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.us.singledigits.myapartment.commons.utils.SocketConstants
import com.us.singledigits.myapartment.data.models.DeviceFromSocket
import com.us.singledigits.myapartment.data.models.DwellingUnitDevice
import com.us.singledigits.myapartment.data.models.DwellingUnitDeviceAttributes
import com.us.singledigits.myapartment.data.models.ServiceMetaData
import com.us.singledigits.myapartment.data.network.api.DevicesApi
import com.us.singledigits.myapartment.data.network.responses.DwellingUnitDeviceResponse
import com.us.singledigits.myapartment.data.network.responses.ServiceResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class DevicesSocketService : Service() {
    private val mBinder: IBinder = MyDevicesServiceBinder()
    private var mSocket: Socket? = null
    var siteDevicesData: List<DwellingUnitDevice>? = null

    // TODO: Handel real dynamic Id
    private val TEMP_ID = "3fa85f64-5717-4562-b3fc-2c963f66afa6"

    private var broadcaster: LocalBroadcastManager? = null

    val ACTION_NETWORK_STATE_CHANGED = "networkStateChanged"

    private val TAG: String = DevicesSocketService::class.java.name

    var isSocketStarted = false

    override fun onCreate() {
        super.onCreate()
        Log.v("DevicesSocketService", "onCreate()")
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mSocket == null) {
            if (isSocketStarted) {
            } else {
                isSocketStarted = true
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG, "onBind")
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter(ACTION_NETWORK_STATE_CHANGED))
        loadServiceDataAndConnect(TEMP_ID)
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "onUnbind")
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        stopSocket()
        return false
    }

    override fun onRebind(intent: Intent?) {
        Log.v(TAG, "onRebind()")
        onBind(intent)
    }

    private fun startSocket(socketMetadata: ServiceMetaData) {
        val opts = IO.Options()
        opts.forceNew = true
        opts.query = "token=" + socketMetadata.websocketToken
        mSocket = IO.socket(socketMetadata.websocketURL, opts)

        mSocket?.on(Socket.EVENT_CONNECT_TIMEOUT) {
            Log.v(TAG, "Socket connection timeout")
        }
        mSocket?.on(Socket.EVENT_RECONNECT_FAILED) {
            Log.v(TAG, "Socket reconnect failed")
        }

        mSocket?.on(Socket.EVENT_CONNECT) {
            Log.d(TAG, "Socket connected")
            DevicesApi().getDevices(TEMP_ID).enqueue(object :
                Callback<DwellingUnitDeviceResponse> {
                override fun onResponse(
                    call: Call<DwellingUnitDeviceResponse>,
                    response: Response<DwellingUnitDeviceResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("SUCCESS_API", "Called getService API successfully")
                        var siteDevices: List<DwellingUnitDevice>? = response.body()?.data
                        if (siteDevices != null) {
                            siteDevices =
                                siteDevices.filter { p -> p.id != null } /* It is usefull because device may be null */
                            siteDevicesData = siteDevices
                            siteDevicesData?.forEach { it.deviceStatus = ArrayList() }
                            siteDevices.forEach {
                                mSocket?.emit("subscribe", it.device.platformIdentifier)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<DwellingUnitDeviceResponse>, t: Throwable) {
                    Log.d(
                        "FAILED_API",
                        "Failed to call getDevices API, can't subscribe to devices, Error: " + t.message
                    )
                }
            })
        }

        mSocket?.on("deviceState") {
            Log.d(TAG, "Socket got device change")
            Log.d(TAG, it[0].toString())
            val deviceChanges = Gson().fromJson(it[0].toString(), DeviceFromSocket::class.java)
            // Broadcast status change
            broadcastDevicesData(deviceChanges)
        }

        mSocket?.on(Socket.EVENT_DISCONNECT) {
            Log.d(TAG, "Socket disconnected," + Arrays.toString(it))
            mSocket?.close()
        }

        mSocket?.on(Socket.EVENT_CONNECT_ERROR) {
            Log.d(TAG, "Socket connection error," + Arrays.toString(it))
        }

        mSocket?.on(Socket.EVENT_ERROR) {
            Log.d(TAG, "Socket event error," + Arrays.toString(it))
            if (mSocket != null) {
                Log.e(TAG, "Error (connection may be lost)")
            }
        }
        mSocket?.connect()
    }

    private fun stopSocket() {
        if (mSocket != null) {
            mSocket?.disconnect()
            mSocket = null
        }
    }

    private fun broadcastDevicesData(deviceStatus: DeviceFromSocket?) {
        Log.d(TAG, "broadcastDevicesData(), DeviceDataToBroadcast = ${deviceStatus.toString()}")
        val intent = Intent()
        if (deviceStatus != null) {
            intent.putExtra("deviceLiveStatus", deviceStatus)
            intent.action = "listenToDevicesStatus"
        }
        broadcaster?.sendBroadcast(intent)
    }

    private fun loadServiceDataAndConnect(id: String) {
        DevicesApi().getService(id).enqueue(object :
            Callback<ServiceResponse> {
            override fun onResponse(
                call: Call<ServiceResponse>,
                response: Response<ServiceResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getService API successfully")
                    var serviceMetadata: ServiceMetaData? = null
                    val siteService: List<com.us.singledigits.myapartment.data.models.Service>? =
                        response.body()?.data
                    if (siteService != null) {
                        val listIterator = siteService[0].attributes.listIterator()
                        while (listIterator.hasNext()) {
                            val attribute = listIterator.next()
                            if (attribute.type == "iot") {
                                serviceMetadata = attribute.metaData
                                break
                            }
                        }
                    }
                    if (serviceMetadata != null) {
                        Log.d(
                            "SOCKET",
                            "Socket details available, will connect to URL:" + serviceMetadata.websocketURL
                        )
                        startSocket(serviceMetadata)
                    } else {
                        Log.d("SOCKET", "No iot service details available")
                    }
                }
            }

            override fun onFailure(call: Call<ServiceResponse>, t: Throwable) {
                Log.d(
                    "FAILED_API",
                    "Failed to call getService API, can't connect to socket, Error: " + t.message
                )
            }
        })
    }


    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
//            val networkIsOn = intent.getBooleanExtra(ACTION_NETWORK_STATE_CHANGED, false)
//            if (networkIsOn) {
//            startSocket()
//            } else {
//                stopSocket()
//            }
        }
    }

    // To change any device status
    fun changeDeviceStatus(deviceData: DwellingUnitDeviceAttributes, payload: JSONObject) {
        Log.v(TAG, "changeDeviceStatus(), sent deviceData payload to change $deviceData, payload: $payload")
        mSocket?.emit(
            "commandDevice",
            deviceData.platformName,
            deviceData.platformIdentifier,
            deviceData.platformHubIdentifier,
            payload,
            SocketConstants.EMIT_EMAIL.value,
            SocketConstants.EMIT_USERNAME.value,
            SocketConstants.EMIT_PASSWORD.value,
            SocketConstants.EMIT_PORT.value
        )
    }

    fun addOrUpdateDeviceInitialStatusItem(deviceStatusModel: DeviceFromSocket) {
        if (siteDevicesData != null) {
            val size: Int = siteDevicesData!!.size
            for (i in 0 until size) {
                if (siteDevicesData!![i].device.platformIdentifier == deviceStatusModel.deviceID) {
                    if (siteDevicesData!![i].deviceStatus
                            .filter { it.attributeType == deviceStatusModel.attributeType }
                            .any()
                    ) {
                        Log.d("ADD_STATUS", "Attribute with same type exist before in the status list")
                        var statusItems = siteDevicesData!![i].deviceStatus
                        val statusItemsSize: Int = statusItems.size
                        for (j in 0 until statusItemsSize) {
                            if (statusItems[j].attributeType == deviceStatusModel.attributeType) {
                                siteDevicesData!![i].deviceStatus.remove(siteDevicesData!![i].deviceStatus[j])
                                siteDevicesData!![i].deviceStatus.add(deviceStatusModel)
                                break
                            }
                        }
                    } else {
                        siteDevicesData!![i].deviceStatus.add(deviceStatusModel)
                    }
                }
            }
        }
    }

    inner class MyDevicesServiceBinder : Binder() {
        val service: DevicesSocketService
            get() {
                Log.v("MyDevicesBinder", "getService()")
                return this@DevicesSocketService
            }
    }
}
