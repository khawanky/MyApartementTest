package com.us.singledigits.myapartment.ui.home

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.SocketConstants
import com.us.singledigits.myapartment.data.models.DeviceFromSocket
import com.us.singledigits.myapartment.data.models.DwellingUnitDevice
import com.us.singledigits.myapartment.data.services.DevicesSocketService
import com.us.singledigits.myapartment.ui.devices.DevicesActivity
import com.us.singledigits.myapartment.ui.doors.DoorsActivity
import com.us.singledigits.myapartment.ui.lights.LightsActivity
import com.us.singledigits.myapartment.ui.menu.menu_list.MenuActivity
import com.us.singledigits.myapartment.ui.notifications.NotificationsActivity
import com.us.singledigits.myapartment.ui.thermostat.ThermostatActivity
import com.us.singledigits.myapartment.ui.tvguide.TvguideActivity
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class HomePageActivity : AppCompatActivity() {
    private val deviceStatusReceiver = DevicesSocketServiceReceiver()
    private lateinit var devicesSocketService: DevicesSocketService
    private var devicesSocketBound = false

    private var devicesInfoData: List<DwellingUnitDevice>? = null
    private var doorsInfoData: List<DwellingUnitDevice>? = null
    private var lightsInfoData: List<DwellingUnitDevice>? = null
    private var thermostatInfoData: List<DwellingUnitDevice>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        ivHamburger.setOnClickListener {
            startActivity(Intent(applicationContext, MenuActivity::class.java))
        }

//      TODO: Check here which notification Icon to view
        var haveNotification = false
        if(haveNotification){
            ivNotification.setImageResource(R.drawable.notification)
        }

        ivNotification.setOnClickListener {
            startActivity(Intent(applicationContext, NotificationsActivity::class.java))
        }

        doorsTileLayout.setOnClickListener {
            // Sent concerned doors devices to light
            val intent = Intent(this, DoorsActivity::class.java)
            intent.putExtra("doorsDevicesInfo", doorsInfoData as ArrayList<DwellingUnitDevice>)
            startActivity(intent)
        }

        lightsTileLayout.setOnClickListener {
            // Sent concerned lights devices to light
            val intent = Intent(this, LightsActivity::class.java)
            intent.putExtra("lightsDevicesInfo", lightsInfoData as ArrayList<DwellingUnitDevice>)
            startActivity(intent)
        }

        thermostatTileLayout.setOnClickListener {
            val intent = Intent(this, ThermostatActivity::class.java)
            intent.putExtra(
                "thermostatDeviceInfo",
                thermostatInfoData as ArrayList<DwellingUnitDevice>
            )
            startActivity(intent)
        }

        devicesTileLayout.setOnClickListener {
            startActivity(Intent(this, DevicesActivity::class.java))
        }

        tvGuideTileLayout.setOnClickListener {
            startActivity(Intent(this, TvguideActivity::class.java))
        }

        // to receive messages from the socket connection (device updates)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceStatusReceiver, IntentFilter("listenToDevicesStatus"))
    }

    override fun onStart() {
        super.onStart()
        Intent(this, DevicesSocketService::class.java).also { intent ->
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(mServiceConnection)
        devicesSocketBound = false
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as DevicesSocketService.MyDevicesServiceBinder
            devicesSocketService = binder.service
            devicesSocketBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            devicesSocketBound = false
        }
    }

    private fun updateHomeTilesUI(
        doorsInfoData: List<DwellingUnitDevice>?,
        lightsInfoData: List<DwellingUnitDevice>?,
        thermostatInfoData: List<DwellingUnitDevice>?
    ) {
        val totalDoorsCount = doorsInfoData?.size
        var totalUnlockedDoors = 0
        doorsInfoData?.forEach {
            totalUnlockedDoors += it.deviceStatus.filter { p ->
                p.attributeType == SocketConstants.IOT_ATTR_TYPE_LOCK.value &&
                        p.value.startsWith(SocketConstants.IOT_ATTR_VALUE_LOCK_UNLOCKED.value)
            }.count()
        }

        val totalLightsCount = lightsInfoData?.size
        var totalOnLights = 0
        lightsInfoData?.forEach {
            totalOnLights += it.deviceStatus.filter { p -> p.value == SocketConstants.IOT_ATTR_VALUE_SWITCH_ON.value }
                .count()
        }

        Log.i(
            "IotCounts",
            "totalDoorsCount=" + totalDoorsCount.toString() + ", totalUnLockedDoors=" + totalUnlockedDoors.toString() + ", totalLightsCount="
                    + totalLightsCount.toString() + ", totalOnLights=" + totalOnLights.toString()
        )

        if (totalUnlockedDoors == 0) { // All Locked
            tvDoorsStatus.setText(R.string.doorsAllLocked)
            ivDoorsStatusIcon.setImageResource(R.drawable.locked)
        } else {
            val statusText = totalUnlockedDoors.toString() + " " + getString(R.string.doorsUnlocked)
            tvDoorsStatus.text = statusText
            ivDoorsStatusIcon.setImageResource(R.drawable.unlocked)
        }

        if (totalOnLights == 0) { // All Off
            tvLightsStatus.setText(R.string.lightsAllOff)
            ivLightsStatusIcon.setImageResource(R.drawable.closed_lamp)
        } else {
            val statusText = totalOnLights.toString() + " " + getString(R.string.lightsOn)
            tvLightsStatus.text = statusText
            ivLightsStatusIcon.setImageResource(R.drawable.opened_lamp)
        }

        if (thermostatInfoData != null && thermostatInfoData.isNotEmpty()) {
            if (thermostatInfoData.size == 1) {
                Log.d("THERMOSTAT_STATUS", thermostatInfoData[0].deviceStatus.toString())
                val statusItems = thermostatInfoData[0].deviceStatus
                val statusItemsSize: Int = statusItems.size
                for (i in 0 until statusItemsSize) {
                    if (statusItemsSize > 0) {
                        when (statusItems[i].attributeType) {
                            SocketConstants.IOT_ATTR_TYPE_TEMP.value -> {
                                Log.d("THERMOSTAT_TEMP", statusItems[i].toString())
                                tvThermostatTemperature.text =
                                    statusItems[i].value.toFloat().roundToInt().toString()
                            }
                            SocketConstants.IOT_ATTR_TYPE_THERMO_OP_STATE_CHANGED.value -> {
                                Log.d("THERMOSTAT_CHANGE", statusItems[i].toString())
                                when (statusItems[i].value) {
                                    SocketConstants.IOT_ATTR_VALUE_THERMO_STATUS_IDLE.value -> {
                                        tvThermostatStatus.text =
                                            getString(R.string.thermostatHomeHolding)
                                        tvThermostatTemperature.setTextColor(getColor(R.color.thermostatHomeHolding))
                                    }
                                    SocketConstants.IOT_ATTR_VALUE_THERMO_STATUS_HEATING.value -> {
                                        tvThermostatStatus.text =
                                            getString(R.string.thermostatHomeHeating)
                                        tvThermostatTemperature.setTextColor(getColor(R.color.thermostatHomeHeating))
                                    }
                                    SocketConstants.IOT_ATTR_VALUE_THERMO_STATUS_COOLING.value -> {
                                        tvThermostatStatus.text =
                                            getString(R.string.thermostatHomeCooling)
                                        tvThermostatTemperature.setTextColor(getColor(R.color.thermostatHomeCooling))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this@HomePageActivity, "There are multiple thermostat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class DevicesSocketServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val deviceStatusChange = intent.getSerializableExtra("deviceLiveStatus")
            if (deviceStatusChange != null) {
                val deviceStatusModel = deviceStatusChange as DeviceFromSocket
                Log.d(
                    "HOMEPAGE_ACTIVITY",
                    "RECEIVED_STATUS_CHANGE from socket = ${deviceStatusModel.toString()}"
                )
                devicesSocketService.addOrUpdateDeviceInitialStatusItem(deviceStatusModel)
            }

//            This is just evaluating the status count and change UI initially
            devicesInfoData = devicesSocketService.siteDevicesData
            doorsInfoData =
                devicesInfoData?.filter { p -> p.device.function == SocketConstants.IOT_FUNCTION_LOCK.value }
            lightsInfoData = devicesInfoData?.filter { p ->
                p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_TOGGLE.value
                        || p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_DIMMER.value
            }

            thermostatInfoData = devicesInfoData?.filter { p ->
                p.device.function == SocketConstants.IOT_FUNCTION_THERMOSTAT.value
            }

            updateHomeTilesUI(doorsInfoData, lightsInfoData, thermostatInfoData)
        }
    }
}

