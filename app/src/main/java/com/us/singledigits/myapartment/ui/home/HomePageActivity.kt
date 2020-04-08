package com.us.singledigits.myapartment.ui.home

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.SparseArray
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.commons.utils.SocketConstants
import com.us.singledigits.myapartment.data.models.DeviceFromSocket
import com.us.singledigits.myapartment.data.models.DwellingUnitDevice
import com.us.singledigits.myapartment.data.network.api.MduApi
import com.us.singledigits.myapartment.data.network.responses.DwellingUnitDeviceResponse
import com.us.singledigits.myapartment.data.services.DevicesSocketService
import com.us.singledigits.myapartment.ui.devices.DevicesViewModel
import com.us.singledigits.myapartment.ui.menu.menu_list.MenuActivity
import com.us.singledigits.myapartment.ui.notifications.NotificationsActivity
import com.us.singledigits.myapartment.ui.notifications.NotificationsViewModel
import kotlinx.android.synthetic.main.activity_home_page.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt


class HomePageActivity : BaseActivity() {
    private val deviceStatusReceiver = DevicesSocketServiceReceiver()
    private lateinit var devicesSocketService: DevicesSocketService
    private var devicesSocketBound = false
    private var notificationsViewModel: NotificationsViewModel? = null

    private var iotDevicesInfoData: List<DwellingUnitDevice>? = null
    private var doorsInfoData: List<DwellingUnitDevice>? = null
    private var lightsInfoData: List<DwellingUnitDevice>? = null
    private var thermostatsInfoData: List<DwellingUnitDevice>? = null
    private var fansInfoData: List<DwellingUnitDevice>? = null
    private var outletsInfoData: List<DwellingUnitDevice>? = null

    private var homePageTilesInformation: SparseArray<HomeTileItem> = SparseArray(15)
    private var recyclerViewManager: GridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        loadSharedPreferenceData()

        // Hamburger menu
        ivHamburger.setOnClickListener {
            startActivity(Intent(applicationContext, MenuActivity::class.java))
        }

        // Notifications
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        notificationsViewModel?.hasUnreadNotifications(token, residentModel)
            ?.observe(this, Observer<Boolean> {
                if (it != null && it == true) {
                    ivNotification.setImageResource(R.drawable.notification)
                }
            })

        ivNotification.setOnClickListener {
            startActivity(Intent(applicationContext, NotificationsActivity::class.java))
        }

        // Tiles
        recyclerViewManager = GridLayoutManager(this, 2)
        rvHomePageTiles.layoutManager = recyclerViewManager
        rvHomePageTiles.setHasFixedSize(true)

        var tvGuideTileInfo = HomeTileItem(
            getString(R.string.tvGuide), "",R.color.black5,
            "", R.drawable.tv_guide, getString(R.string.tvGuideTileName),
            true, null
        )

        var devicesTileInfo = HomeTileItem(
            getString(R.string.devices),
            "0 " + getString(R.string.devicesTotal),R.color.black5,
            "", R.drawable.devices, getString(R.string.devicesTileName),
            true, null
        )

        addOrUpdateTile(tvGuideTileInfo)
        addOrUpdateTile(devicesTileInfo)

        // Change total number of devices tile
        val devicesViewModel: DevicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        devicesViewModel.getMyDevicesItemsCount(token, residentModel)?.observe(this, Observer<Int> {
            if (it != null) {
                devicesTileInfo.status = "$it " + getString(R.string.devicesTotal)
                addOrUpdateTile(devicesTileInfo)
            }
        })

        MduApi().getDwellingUnitDevices(token, residentModel?.links?.dwellingUnitDevices).enqueue(object :
            Callback<DwellingUnitDeviceResponse> {
            override fun onResponse(call: Call<DwellingUnitDeviceResponse>,
                                    response: Response<DwellingUnitDeviceResponse>) {
                if (response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called getDwellingUnitDevices API successfully")
                    var siteDevices: List<DwellingUnitDevice>? = response.body()?.data
                    if (siteDevices != null) {
                        siteDevices = siteDevices.filter { p -> p.id != null } /* It is usefull because device may be null */
                        siteDevices.forEach {
                            initializeIotTiles(getIotType(it.device.function))
                        }
                        if(doorsTileInfo != null) addOrUpdateTile(doorsTileInfo!!)
                        if(lightsTileInfo != null) addOrUpdateTile(lightsTileInfo!!)
                        if(thermostatsTileInfo != null) addOrUpdateTile(thermostatsTileInfo!!)
                        if(fansTileInfo != null) addOrUpdateTile(fansTileInfo!!)
                        if(outletsTileInfo != null) addOrUpdateTile(outletsTileInfo!!)
                    }
                }
            }
            override fun onFailure(call: Call<DwellingUnitDeviceResponse>, t: Throwable) {
                Log.d(
                    "FAILED_API", "Failed to call getDevices API, can't subscribe to devices, Error: " + t.message
                )
            }
        })

        // to receive messages from the socket connection (device updates)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceStatusReceiver, IntentFilter("listenToDevicesStatus"))
    }


    private var doorsTileInfo:HomeTileItem?=null
    private var lightsTileInfo:HomeTileItem?=null
    private var thermostatsTileInfo:HomeTileItem?=null
    private var fansTileInfo:HomeTileItem?=null
    private var outletsTileInfo:HomeTileItem?=null

    private fun initializeIotTiles (iotType:String?) {
        when (iotType) {
            getString(R.string.doorsTileName) -> {
                if(doorsTileInfo == null) {
                    doorsTileInfo = HomeTileItem(getString(R.string.doors), getString(R.string.dashes), R.color.black5,"",
                        R.drawable.locked, getString(R.string.doorsTileName), false, null)
                }
            }
            getString(R.string.lightsTileName) -> {
                if(lightsTileInfo == null) {
                    lightsTileInfo = HomeTileItem(
                        getString(R.string.lights), getString(R.string.dashes), R.color.black5,"",
                        R.drawable.closed_lamp, getString(R.string.lightsTileName), false, null
                    )
                }
            }
            getString(R.string.thermostatsTileName) -> {
                if(thermostatsTileInfo == null) {
                    thermostatsTileInfo = HomeTileItem(
                        getString(R.string.thermostat), getString(R.string.dashes),R.color.black5,
                        "_", 0,
                        getString(R.string.thermostatsTileName), false, null
                    )
                }
            }
            getString(R.string.fansTileName) -> {
                if(fansTileInfo == null) {
                    fansTileInfo = HomeTileItem(
                        getString(R.string.fans), getString(R.string.dashes),R.color.black5, "", 0,
                        getString(R.string.fansTileName), false, null
                    )
                }
            }
            getString(R.string.outletsTileName) -> {
                if(outletsTileInfo == null) {
                    outletsTileInfo = HomeTileItem(
                        getString(R.string.outlets), getString(R.string.dashes), R.color.black5,"",  0,
                        getString(R.string.outletsTileName), false, null
                    )
                }
            }
        }
    }

    // Update iot tiles UI
    private fun updateDoorsTile() {
        doorsInfoData = iotDevicesInfoData?.filter { p -> p.device.function == SocketConstants.IOT_FUNCTION_LOCK.value }
        Log.d("HOME_DOOR_RECEIVED", "Data: $doorsInfoData")
        doorsTileInfo?.iotTileDevices = doorsInfoData

        val totalDoorsCount = doorsInfoData?.size
        var totalUnlockedDoors = 0
        doorsInfoData?.forEach {
            totalUnlockedDoors += it.deviceStatus.filter { p ->
                p.attributeType == SocketConstants.IOT_ATTR_TYPE_LOCK.value &&
                        p.value.startsWith(SocketConstants.IOT_ATTR_VALUE_LOCK_UNLOCKED.value)
            }.count()
        }

        if (totalUnlockedDoors == 0) { // All Locked
            doorsTileInfo?.statusIconResource = R.drawable.locked
            if (totalDoorsCount == 1) {
                doorsTileInfo?.title = getString(R.string.door)
                doorsTileInfo?.status = getString(R.string.doorsLocked)
            } else {
                doorsTileInfo?.status = getString(R.string.doorsAllLocked)
            }
        } else {  // Any Unlocked
            doorsTileInfo?.statusIconResource = R.drawable.unlocked
            if (totalDoorsCount == 1) {
                doorsTileInfo?.title = getString(R.string.door)
                doorsTileInfo?.status = getString(R.string.doorsUnlocked)
            } else {
                if (totalUnlockedDoors == totalDoorsCount) {
                    doorsTileInfo?.status = getString(R.string.doorsAllUnlocked)
                } else {
                    doorsTileInfo?.status = totalUnlockedDoors.toString() + " " + getString(R.string.doorsUnlocked)
                }
            }
        }
        doorsTileInfo?.isEnabled = true
    }

    private fun updateLightsTile(){
        lightsInfoData = iotDevicesInfoData?.filter { p -> p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_TOGGLE.value
                || p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_DIMMER.value }
        Log.d("HOME_LIGHT_RECEIVED", "Data: $lightsInfoData")
        lightsTileInfo?.iotTileDevices = lightsInfoData

        val totalLightsCount = lightsInfoData?.size
        var totalOnLights = 0
        lightsInfoData?.forEach {
            totalOnLights += it.deviceStatus.filter { p -> p.value == SocketConstants.IOT_ATTR_VALUE_SWITCH_ON.value }.count()
        }

        // Lights tile logic
        if (totalOnLights == 0) { // All Off
            lightsTileInfo?.statusIconResource = R.drawable.closed_lamp
            if (totalLightsCount == 1) {
                lightsTileInfo?.title = getString(R.string.light)
                lightsTileInfo?.status = getString(R.string.lightsOff)

            } else {
                lightsTileInfo?.status = getString(R.string.lightsAllOff)
            }
        } else { // Any On
            lightsTileInfo?.statusIconResource = R.drawable.opened_lamp
            if (totalLightsCount == 1) {
                lightsTileInfo?.title = getString(R.string.light)
                lightsTileInfo?.status = getString(R.string.lightsOn)
            } else {
                if (totalOnLights == totalLightsCount) {
                    lightsTileInfo?.status = getString(R.string.lightsAllOn)
                } else {
                    lightsTileInfo?.status = totalOnLights.toString() + " " + getString(R.string.lightsOn)
                }
            }
        }
        lightsTileInfo?.isEnabled = true
    }

    var hasShownError = false
    private fun updateThermostatsTile() {
        thermostatsInfoData = iotDevicesInfoData?.filter { p -> p.device.function == SocketConstants.IOT_FUNCTION_THERMOSTAT.value }
        Log.d("HOME_THERMO_RECEIVED", "Data: $thermostatsInfoData")
        thermostatsTileInfo?.iotTileDevices = thermostatsInfoData

        if (thermostatsInfoData != null && thermostatsInfoData!!.isNotEmpty()) {
            if (thermostatsInfoData?.size == 1) {
                Log.d("THERMOSTAT_STATUS", thermostatsInfoData!![0].deviceStatus.toString())

                val statusItems = thermostatsInfoData!![0].deviceStatus
                val statusItemsSize: Int = statusItems.size
                for (i in 0 until statusItemsSize) {
                    if (statusItemsSize > 0) {
                        when (statusItems[i].attributeType) {
                            SocketConstants.IOT_ATTR_TYPE_THERMO_MODE.value -> {
                                Log.d("THERMO_HOME_MODE", statusItems[i].toString())
                                when (statusItems[i].value) {
                                    SocketConstants.THERMOSTAT_MODE_OFF.value -> {
                                        thermostatsTileInfo?.status = getString(R.string.thermostatModeOff)
                                        thermostatsTileInfo?.statusColor = R.color.black5
                                    }
                                    SocketConstants.THERMOSTAT_MODE_HEAT.value -> {
                                        thermostatsTileInfo?.status = getString(R.string.thermostatModeHeat)
                                        thermostatsTileInfo?.statusColor = R.color.thermostatHomeHeatMode
                                    }
                                    SocketConstants.THERMOSTAT_MODE_COOL.value -> {
                                        thermostatsTileInfo?.status = getString(R.string.thermostatModeCool)
                                        thermostatsTileInfo?.statusColor = R.color.thermostatHomeCoolMode
                                    }
                                }
                            }
                            SocketConstants.IOT_ATTR_TYPE_TEMP.value -> {
                                Log.d("THERMO_HOME_TEMP", statusItems[i].toString())
                                thermostatsTileInfo?.level = statusItems[i].value.toFloat().roundToInt().toString()
                            }
                        }
                    }
                }
                thermostatsTileInfo?.isEnabled = true
            } else {
                if (!hasShownError) Toast.makeText(this@HomePageActivity, "There are multiple thermostat", Toast.LENGTH_SHORT).show()
                thermostatsTileInfo?.isEnabled = false
                hasShownError = true
            }
        }
    }

    private fun updateFansTile() {
        fansInfoData = iotDevicesInfoData?.filter { p -> p.device.function == SocketConstants.IOT_FUNCTION_FAN.value }
        Log.d("HOME_FAN_RECEIVED", "Data: $fansInfoData")

    }

    private fun updateOutletsTile() {
        outletsInfoData = iotDevicesInfoData?.filter { p -> p.device.function == SocketConstants.IOT_FUNCTION_OUTLET.value }
        Log.d("HOME_OUTLET_RECEIVED", "Data: $outletsInfoData")

    }

    private fun updateIotTiles(iotType:String?){
        when (iotType) {
            getString(R.string.doorsTileName) -> {
                updateDoorsTile()
                addOrUpdateTile(doorsTileInfo!!)
            }
            getString(R.string.lightsTileName) -> {
                updateLightsTile()
                addOrUpdateTile(lightsTileInfo!!)
            }
            getString(R.string.thermostatsTileName) -> {
                updateThermostatsTile()
                addOrUpdateTile(thermostatsTileInfo!!)
            }
            getString(R.string.fansTileName) -> {
                updateFansTile()
                addOrUpdateTile(fansTileInfo!!)
            }
            getString(R.string.outletsTileName) -> {
                updateOutletsTile()
                addOrUpdateTile(outletsTileInfo!!)
            }
        }
    }

    private fun <C> asList(sparseArray: SparseArray<C>?): LinkedList<C> {
        val linkedList: LinkedList<C> = LinkedList()
        for (i in 0 until sparseArray!!.size()) linkedList.add(sparseArray.valueAt(i))
        return linkedList
    }

    private fun addOrUpdateTile(tile: HomeTileItem) {
        homePageTilesInformation.put(getTileKey(tile.tileName), tile)
        rvHomePageTiles.adapter = RecyclerHomeTilesAdapter(asList(homePageTilesInformation))
        rvHomePageTiles.adapter?.notifyDataSetChanged()
    }

    private fun getIotType (function:String) : String? {
        return when (function) {
            SocketConstants.IOT_FUNCTION_LOCK.value -> getString(R.string.doorsTileName)
            SocketConstants.IOT_FUNCTION_LIGHT_TOGGLE.value -> getString(R.string.lightsTileName)
            SocketConstants.IOT_FUNCTION_LIGHT_DIMMER.value  -> getString(R.string.lightsTileName)
            SocketConstants.IOT_FUNCTION_THERMOSTAT.value -> getString(R.string.thermostatsTileName)
            SocketConstants.IOT_FUNCTION_FAN.value -> getString(R.string.fansTileName)
            SocketConstants.IOT_FUNCTION_OUTLET.value -> getString(R.string.outletsTileName)
            else -> null
        }
    }

    private fun getTileKey (tileName:String) : Int {
        return when (tileName) {
            getString(R.string.doorsTileName) -> 1
            getString(R.string.lightsTileName) -> 2
            getString(R.string.thermostatsTileName) -> 3
            getString(R.string.devicesTileName) -> 4
            getString(R.string.tvGuideTileName) -> 5
            getString(R.string.fansTileName) -> 6
            getString(R.string.outletsTileName) -> 7
            else -> 0
        }
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

    inner class DevicesSocketServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val deviceStatusChange = intent.getSerializableExtra("deviceLiveStatus")
            var functionChanged = ""
            if (deviceStatusChange != null) {
                val deviceStatusModel = deviceStatusChange as DeviceFromSocket
                Log.d(
                    "HOMEPAGE_ACTIVITY",
                    "RECEIVED_STATUS_CHANGE from socket = $deviceStatusModel"
                )
                 functionChanged = devicesSocketService.addOrUpdateDeviceInitialStatusItem(deviceStatusModel)
            }
            iotDevicesInfoData = devicesSocketService.siteDevicesData
            updateIotTiles(getIotType(functionChanged))
        }
    }
}

