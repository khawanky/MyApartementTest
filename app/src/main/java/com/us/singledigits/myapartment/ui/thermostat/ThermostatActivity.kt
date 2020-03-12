package com.us.singledigits.myapartment.ui.thermostat

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.CircularSeekBar
import com.us.singledigits.myapartment.commons.utils.SocketConstants
import com.us.singledigits.myapartment.data.models.DeviceFromSocket
import com.us.singledigits.myapartment.data.models.DwellingUnitDevice
import com.us.singledigits.myapartment.data.models.DwellingUnitDeviceAttributes
import com.us.singledigits.myapartment.data.services.DevicesSocketService
import kotlinx.android.synthetic.main.activity_thermostat.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.backActionBar
import kotlinx.android.synthetic.main.toolbar_with_backarrow.toolbar_title
import kotlinx.android.synthetic.main.toolbar_with_white_backarrow.*
import org.json.JSONObject
import kotlin.math.roundToInt

class ThermostatActivity : AppCompatActivity() {
    private val deviceStatusReceiver = DevicesSocketServiceReceiver()
    private lateinit var devicesSocketService: DevicesSocketService
    private var devicesSocketBound = false
    private var thermostatInfoData: List<DwellingUnitDevice>? = null

    //    Thermostat returned data
    var thermostatMode=SocketConstants.THERMOSTAT_MODE_OFF.value // cool, heat, off
    var thermostatStatus="" // heating, cooling, idle
    var thermostatSetPoint=""
    var thermostatTemperature=""
    var deviceId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thermostat)
        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.thermostat)
        ivBackArrow.setOnClickListener{
            this.finish()
        }

        circulareSeekThermostat.progress = circulareSeekThermostat.progressMin

        val thermostatDeviceInfo = intent.extras?.getSerializable("thermostatDeviceInfo") as ArrayList<DwellingUnitDevice>
        updateThermostatUI(thermostatDeviceInfo)
        val deviceAttributes = thermostatDeviceInfo[0].device
        deviceId = deviceAttributes.platformIdentifier

        var handler = Handler()

        ivMinus.setOnClickListener{
            circulareSeekThermostat.primaryProgress ++
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed({
                loadingWaitingForSocketResponse(true)
                thermostatSetPoint = circulareSeekThermostat.primaryProgress.toString()
                submitThermostatChangesToWebSocket(deviceAttributes)
            }, 3000)
        }

        ivPlus.setOnClickListener{
            circulareSeekThermostat.primaryProgress --
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed({
                loadingWaitingForSocketResponse(true)
                thermostatSetPoint = circulareSeekThermostat.primaryProgress.toString()
                submitThermostatChangesToWebSocket(deviceAttributes)
            }, 3000)
        }

        modeOff.setOnClickListener {
            thermostatMode = SocketConstants.THERMOSTAT_MODE_OFF.value
            loadingWaitingForSocketResponse(true)
            submitThermostatChangesToWebSocket(deviceAttributes)
        }
        modeHeat.setOnClickListener {
            thermostatMode = SocketConstants.THERMOSTAT_MODE_HEAT.value
            loadingWaitingForSocketResponse(true)
            thermostatSetPoint = circulareSeekThermostat.primaryProgress.toString()
            submitThermostatChangesToWebSocket(deviceAttributes)
        }
        modeCool.setOnClickListener {
            thermostatMode = SocketConstants.THERMOSTAT_MODE_COOL.value
            loadingWaitingForSocketResponse(true)
            thermostatSetPoint = circulareSeekThermostat.primaryProgress.toString()
            submitThermostatChangesToWebSocket(deviceAttributes)
        }

        circulareSeekThermostat.onCircularSeekBarChangeListener = object:  CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar,
                                           primaryProgress: Float, secondaryProgress: Float,
                                           fromUser: Boolean) {
                tvCurrentTemp.text = primaryProgress.roundToInt().toString()
                handleTheLinePositionAndBackgourdColor()
            }
            override fun onStartTrackingTouch(circularSeekBar: CircularSeekBar) {
            }
            override fun onStopTrackingTouch(circularSeekBar: CircularSeekBar) {
                if(circulareSeekThermostat.primaryProgress.roundToInt() != circulareSeekThermostat.secondaryProgress.roundToInt()) {
                    loadingWaitingForSocketResponse(true)
                    thermostatSetPoint = circulareSeekThermostat.primaryProgress.toString()
                    submitThermostatChangesToWebSocket(deviceAttributes)
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(deviceStatusReceiver, IntentFilter("listenToDevicesStatus"))
    }

    private fun changeModeButtonsState () {
        when(thermostatMode) {
            SocketConstants.THERMOSTAT_MODE_OFF.value -> {
                circulareSeekThermostat.isEnabled = false
                circulareSeekThermostat.showThumbs = false
                // The buttons backgrounds
                modeOff.setBackgroundResource(R.drawable.circular_thermostat_mode)
                modeHeat.setBackgroundResource(0)
                modeCool.setBackgroundResource(0)
                // The buttons images
                ivModeOff.setImageResource(R.drawable.ic_off_gray)
                ivModeHeat.setImageResource(R.drawable.ic_heat_white)
                ivModeCool.setImageResource(R.drawable.ic_cool_white)
            }

            SocketConstants.THERMOSTAT_MODE_HEAT.value -> {
                circulareSeekThermostat.isEnabled = true
                circulareSeekThermostat.showThumbs = true
                // The buttons backgrounds
                modeOff.setBackgroundResource(0)
                modeHeat.setBackgroundResource(R.drawable.circular_thermostat_mode)
                modeCool.setBackgroundResource(0)
                // The buttons images
                ivModeOff.setImageResource(R.drawable.ic_off_white)
                ivModeHeat.setImageResource(R.drawable.ic_heat_orange)
                ivModeCool.setImageResource(R.drawable.ic_cool_white)
            }

            SocketConstants.THERMOSTAT_MODE_COOL.value -> {
                circulareSeekThermostat.isEnabled = true
                circulareSeekThermostat.showThumbs = true
                // The buttons backgrounds
                modeOff.setBackgroundResource(0)
                modeHeat.setBackgroundResource(0)
                modeCool.setBackgroundResource(R.drawable.circular_thermostat_mode)
                // The buttons images
                ivModeOff.setImageResource(R.drawable.ic_off_white)
                ivModeHeat.setImageResource(R.drawable.ic_heat_white)
                ivModeCool.setImageResource(R.drawable.ic_cool_blue)
            }
        }

        handleTheLinePositionAndBackgourdColor()
    }

    private fun loadingWaitingForSocketResponse(beginRequest: Boolean){
        if(beginRequest) {
            tvOff.isClickable = false
            tvCool.isClickable = false
            tvHeat.isClickable = false
            ivMinus.isClickable = false
            ivPlus.isClickable = false
            circulareSeekThermostat.isEnabled = false
            loadingBar.visibility = View.VISIBLE
            val handler = Handler()
            handler.postDelayed({
                tvOff.isClickable = true
                tvCool.isClickable = true
                tvHeat.isClickable = true
                ivMinus.isClickable = true
                ivPlus.isClickable = true
                circulareSeekThermostat.isEnabled = true
                loadingBar.visibility = View.GONE
                updateThermostatUI(thermostatInfoData)
            }, 10000)
        } else {
            tvOff.isClickable = true
            tvCool.isClickable = true
            tvHeat.isClickable = true
            ivMinus.isClickable = true
            ivPlus.isClickable = true
            circulareSeekThermostat.isEnabled = true
            loadingBar.visibility = View.GONE
        }
    }

    private fun submitThermostatChangesToWebSocket (deviceAttributes: DwellingUnitDeviceAttributes) {
        val jsonPayload = JSONObject()
        jsonPayload.put(SocketConstants.KEY_COMMAND.value, SocketConstants.THERMOSTAT_COMMAND.value)
        jsonPayload.put(SocketConstants.KEY_DEVICE_TYPE.value, SocketConstants.THERMOSTAT_DEVICE_TYPE.value)
        jsonPayload.put(SocketConstants.IOT_ATTR_TYPE_THERMO_MODE.value, thermostatMode)

        if (thermostatMode == SocketConstants.IOT_ATTR_VALUE_THERMO_MODE_HEAT.value) {
            jsonPayload.put(SocketConstants.IOT_ATTR_TYPE_HEATING_SETPOINT.value, thermostatSetPoint)
        } else if (thermostatMode == SocketConstants.IOT_ATTR_VALUE_THERMO_MODE_COOL.value) {
            jsonPayload.put(SocketConstants.IOT_ATTR_TYPE_COOLING_SETPOINT.value, thermostatSetPoint)
        }

        devicesSocketService.changeDeviceStatus(deviceAttributes, jsonPayload)
    }

    private fun handleTheLinePositionAndBackgourdColor() {
        rootLayout.setBackgroundResource(R.color.thermostatHoldingAtBack)
        when(thermostatMode){
            SocketConstants.THERMOSTAT_MODE_OFF.value -> {
                tvStatus.text = ""
            }
            SocketConstants.THERMOSTAT_MODE_HEAT.value -> {
                when (thermostatStatus) {
                    SocketConstants.IOT_ATTR_VALUE_THERMO_STATUS_HEATING.value -> {
                        tvStatus.text = getString(R.string.thermostatHeating)
                        rootLayout.setBackgroundResource(R.color.thermostatHeatingToBack)
                    }
                    else -> {
                        tvStatus.text = getString(R.string.thermostatHolding)
                    }
                }
            }
            SocketConstants.THERMOSTAT_MODE_COOL.value -> {
                when (thermostatStatus) {
                    SocketConstants.IOT_ATTR_VALUE_THERMO_STATUS_COOLING.value -> {
                        tvStatus.text = getString(R.string.thermostatCooling)
                        rootLayout.setBackgroundResource(R.color.thermostatCoolingToBack)
                    }
                    else -> {
                        tvStatus.text = getString(R.string.thermostatHolding)
                    }
                }
            }
        }
    }

    private fun updateThermostatUI(thermostatInfoData: List<DwellingUnitDevice>?) {
        if(thermostatInfoData?.size == 1) {
            tvDeviceName.text = thermostatInfoData[0].device.name
            val statusItems = thermostatInfoData[0].deviceStatus
            val statusItemsSize: Int = statusItems.size

            for (i in 0 until statusItemsSize) {
                if (statusItemsSize > 0) {
                    when(statusItems[i].attributeType) {
                        SocketConstants.IOT_ATTR_TYPE_THERMO_MODE.value -> {
                            Log.d("THERMO_UI_MODE",statusItems[i].toString())
                            thermostatMode=statusItems[i].value
                            changeModeButtonsState()
                        }
                        SocketConstants.IOT_ATTR_TYPE_THERMO_OP_STATE_CHANGED.value -> {
                            Log.d("THERMOSTAT_CHANGE",statusItems[i].toString())
                            thermostatStatus = statusItems[i].value
                            changeModeButtonsState()
                        }
                        SocketConstants.IOT_ATTR_TYPE_THERMO_SETPOINT.value -> {
                            Log.d("THERMOSTAT_SETPOINT",statusItems[i].toString())
                            thermostatSetPoint =  statusItems[i].value
                            if(thermostatMode != SocketConstants.THERMOSTAT_MODE_OFF.value) {
                                circulareSeekThermostat.primaryProgress = thermostatSetPoint.toFloat().roundToInt().toFloat()
                                tvCurrentTemp.text = thermostatSetPoint.toFloat().roundToInt().toString()
                            }
                        }
                        SocketConstants.IOT_ATTR_TYPE_TEMP.value -> {
                            Log.d("THERMOSTAT_TEMP",statusItems[i].toString())
                            thermostatTemperature = statusItems[i].value
                            if(thermostatMode == SocketConstants.THERMOSTAT_MODE_OFF.value) {
                                circulareSeekThermostat.progress = thermostatTemperature.toFloat().roundToInt().toFloat()
                                tvCurrentTemp.text = thermostatTemperature.toFloat().roundToInt().toString()
                            } else {
                                circulareSeekThermostat.secondaryProgress = thermostatTemperature.toFloat().roundToInt().toFloat()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, DevicesSocketService::class.java).also { intent ->
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as DevicesSocketService.MyDevicesServiceBinder
            devicesSocketService = binder.service
            devicesSocketBound = true
            thermostatInfoData = devicesSocketService.siteDevicesData?.filter { p ->
                p.device.function == SocketConstants.IOT_FUNCTION_THERMOSTAT.value
            }
        }
        override fun onServiceDisconnected(arg0: ComponentName) {
            devicesSocketBound = false
        }
    }

    inner class DevicesSocketServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val deviceStatusChange = intent.getSerializableExtra("deviceLiveStatus")
            if (deviceStatusChange != null) {
                val deviceStatusModel = deviceStatusChange as DeviceFromSocket
                devicesSocketService.addOrUpdateDeviceInitialStatusItem(deviceStatusModel)

                if(deviceStatusModel.deviceID == deviceId) {
                    loadingWaitingForSocketResponse(false)
                }

                thermostatInfoData = devicesSocketService.siteDevicesData?.filter { p ->
                    p.device.function == SocketConstants.IOT_FUNCTION_THERMOSTAT.value
                }
                Log.d("THERMOSTAT_ACTIVITY", "RECEIVED_STATUS_CHANGE from socket = ${deviceStatusModel.toString()}" )
                updateThermostatUI(thermostatInfoData)
            }
        }
    }
}