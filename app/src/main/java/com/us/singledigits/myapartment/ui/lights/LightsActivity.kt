package com.us.singledigits.myapartment.ui.lights

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.transition.TransitionManager
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.VerticalSlider
import com.us.singledigits.myapartment.commons.utils.SocketConstants
import com.us.singledigits.myapartment.data.models.DeviceFromSocket
import com.us.singledigits.myapartment.data.models.DwellingUnitDevice
import com.us.singledigits.myapartment.data.models.DwellingUnitDeviceAttributes
import com.us.singledigits.myapartment.data.services.DevicesSocketService
import kotlinx.android.synthetic.main.activity_lights.*
import kotlinx.android.synthetic.main.lights_widget_button.view.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*
import org.json.JSONObject
import kotlin.collections.ArrayList

class LightsActivity : AppCompatActivity() {
    private val deviceStatusReceiver = DevicesSocketServiceReceiver()
    private lateinit var devicesSocketService: DevicesSocketService
    private var devicesSocketBound = false
    private var lightsInfoData: List<DwellingUnitDevice>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lights)
        // Custom color for status bar of this activity only
        if (android.os.Build.VERSION.SDK_INT >= 21) run {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = getColor(R.color.lightsOffYellowBack)
        }

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.lights)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lightsDevicesInfo =
            intent.extras?.getSerializable("lightsDevicesInfo") as ArrayList<DwellingUnitDevice>

        var hasLightOn = false
        lightsDevicesInfo.forEach {
            val lightView: View = inflater.inflate(R.layout.lights_widget_button, null)
            lightView.tvItemRoom.text = it.device.name
            lightView.tag = it.device.platformIdentifier

            val deviceAttributes = it.device
            val statusItems = it.deviceStatus
            val statusItemsSize: Int = statusItems.size

            for (i in 0 until statusItemsSize) {
                if (statusItems[i].attributeType == SocketConstants.IOT_ATTR_TYPE_SWITCH.value) {
                    if (statusItems[i].value.startsWith(SocketConstants.IOT_ATTR_VALUE_SWITCH_ON.value)) {
                        lightView.tvItemStatus.setText(R.string.on)
                        lightView.ivItemStatusImage.setImageResource(R.drawable.lamp_on)
                        lightView.circularButtonContainer.setBackgroundResource(R.drawable.circular_opened_doors_lights)
                        hasLightOn = true
                        // Draw the settings icon if exists fot this light
                        var haveSettingsIcon = false
                        for (j in 0 until statusItemsSize) {
                            if (statusItems[j].attributeType == SocketConstants.IOT_ATTR_TYPE_SWITCH_LEVEL.value) {
                                haveSettingsIcon = true
                                lightView.ivSettingsIcon.tag = "${it.device.platformIdentifier}_DIMMER_ICON"
                                lightView.ivSettingsIcon.visibility = View.VISIBLE
                                lightSettingsIconOnClickListener(lightView.ivSettingsIcon)
                            }
                        }
                        if (!haveSettingsIcon) {
                            lightView.ivSettingsIcon.visibility = View.INVISIBLE
                        }
                    } else {
                        lightView.tvItemStatus.setText(R.string.off)
                        lightView.ivItemStatusImage.setImageResource(R.drawable.lamp_off)
                        lightView.ivSettingsIcon.visibility = View.INVISIBLE
                        lightView.circularButtonContainer.setBackgroundResource(R.drawable.circular_closed_lights)
                    }
                }
            }
            lightView.circularButtonContainer.setOnClickListener {
                lightClickListener(deviceAttributes, lightView)
            }
            lightsStatusContainer.addView(lightView)
        }

        if (hasLightOn)
            rootLayout.setBackgroundResource(R.color.lightsOnYellowBack)

        setVerticalSliderListener(false)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceStatusReceiver, IntentFilter("listenToDevicesStatus"))
    }

    private var handler: Handler = Handler()
    private var currentProgress: Int = 0

    override fun onStart() {
        super.onStart()
        Intent(this, DevicesSocketService::class.java).also { intent ->
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun setVerticalSliderListener(reset: Boolean) {
        if (!reset) {
            lightPercentageVerticalSlider.onProgressChangeListener =
                object : VerticalSlider.OnSliderProgressChangeListener {
                    override fun onChanged(progress: Int, max: Int) {
                        changeThePercentageTextValueAndPosition(progress)
                        currentProgress = progress

                        mScrollView.requestDisallowInterceptTouchEvent(true)

                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed({
                            val jsonPayload = JSONObject()
                            jsonPayload.put(
                                SocketConstants.KEY_COMMAND.value,
                                SocketConstants.SWITCHES_COMMAND.value
                            )
                            jsonPayload.put(
                                SocketConstants.KEY_DEVICE_TYPE.value,
                                SocketConstants.SWITCHES_DIMMER_DEVICE_TYPE.value
                            )
                            jsonPayload.put(SocketConstants.KEY_ACTION.value, currentProgress)

                            val deviceId =
                                lightPercentageVerticalSlider.tag.toString().split("_")[0]
                            val deviceItem =
                                lightsInfoData?.filter { p -> p.id == deviceId }?.first()

                            if (deviceItem != null)
                                devicesSocketService.changeDeviceStatus(
                                    deviceItem.device,
                                    jsonPayload
                                )

                            loadingWaitingForSocketResponse(true)
                            mScrollView.requestDisallowInterceptTouchEvent(false)
                        }, 500)
                    }
                }
        } else {
            lightPercentageVerticalSlider.onProgressChangeListener = null
        }
    }

    private fun lightClickListener(
        deviceAttributes: DwellingUnitDeviceAttributes,
        lightSwitchContainer: View
    ) {
        resetTheSliderSettingsView()
        val jsonPayload = JSONObject()
        jsonPayload.put(SocketConstants.KEY_COMMAND.value, SocketConstants.SWITCHES_COMMAND.value)
        jsonPayload.put(
            SocketConstants.KEY_DEVICE_TYPE.value,
            SocketConstants.SWITCHES_TOGGLE_DEVICE_TYPE.value
        )
        if (lightSwitchContainer.tvItemStatus.text == getString(R.string.on)) {
            jsonPayload.put(
                SocketConstants.KEY_ACTION.value,
                SocketConstants.SWITCHES_TOGGLE_ACTION_OFF.value
            )
        } else {
            jsonPayload.put(
                SocketConstants.KEY_ACTION.value,
                SocketConstants.SWITCHES_TOGGLE_ACTION_ON.value
            )
        }
        loadingWaitingForSocketResponse(true)
        devicesSocketService.changeDeviceStatus(deviceAttributes, jsonPayload)
    }

    private fun lightSettingsIconOnClickListener(ivSettingsIcon: ImageView) {
        // Configure the settings icon on click listener
        ivSettingsIcon.setOnClickListener {
            setVerticalSliderListener(true)
            TransitionManager.beginDelayedTransition(rootLayout)
            if (it.background == null) { // Setting is not active
                resetTheSliderSettingsView()
                lightPercentageVerticalSlider.visibility = View.VISIBLE
                percentageText.visibility = View.VISIBLE
                ivSettingsIcon.setBackgroundResource(R.drawable.circular_lights_active_settings)
                // Get the current live percentage
                lightPercentageVerticalSlider.tag = "${it.tag}_SLIDER"
                val deviceId = it.tag.toString().split("_")[0]
                val deviceItem = lightsInfoData?.filter { p -> p.id == deviceId }?.first()
                val lightDimmerStatus =
                    deviceItem?.deviceStatus?.filter { p2 -> p2.attributeType == SocketConstants.IOT_ATTR_TYPE_SWITCH_LEVEL.value }
                var lightLevel = 0
                if (lightDimmerStatus?.get(0)?.value != null)
                    lightLevel = lightDimmerStatus.get(0).value.toInt()
                lightPercentageVerticalSlider.progress = lightLevel
                changeThePercentageTextValueAndPosition(lightLevel)
            } else { // settings is active (have the white background)
                lightPercentageVerticalSlider.visibility = View.GONE
                percentageText.visibility = View.GONE
                ivSettingsIcon.setBackgroundResource(0)
            }
            setVerticalSliderListener(false)
        }
    }

    private fun resetTheSliderSettingsView() {
        lightPercentageVerticalSlider.visibility = View.GONE
        for (i in 0 until lightsStatusContainer.children.count()) {
            val lightView: View = lightsStatusContainer.getChildAt(i)
            lightView.ivSettingsIcon.setBackgroundResource(0)
        }
    }

    private fun haveActiveSettingsView(): Boolean {
        for (i in 0 until lightsStatusContainer.children.count()) {
            val lightView: View = lightsStatusContainer.getChildAt(i)
            if (lightView.ivSettingsIcon.background != null) {
                return true
            }
        }
        return false
    }

    private fun handleTheLightsScreenBackground() {
        var allOff = true
        for (i in 0 until lightsStatusContainer.children.count()) {
            val v: View = lightsStatusContainer.getChildAt(i)
            if (v.tvItemStatus.text == getString(R.string.on)) {
                allOff = false
            }
        }
        if (allOff) {
            rootLayout.setBackgroundResource(R.color.lightsOffYellowBack)
        } else {
            rootLayout.setBackgroundResource(R.color.lightsOnYellowBack)
        }
    }

    private fun changeThePercentageTextValueAndPosition(progress: Int) {
        var sliderHeight = lightPercentageVerticalSlider.height
        var upperY = lightPercentageVerticalSlider.y
        var bottomY = upperY + sliderHeight
        if (sliderHeight == 0) { // Not drawn yet TODO: Fix the initial status percentage doesn't appear issue
            sliderHeight = 1800
            upperY = 80F
            bottomY = upperY + sliderHeight
        }
        if (progress > 9 && lightPercentageVerticalSlider.visibility == View.VISIBLE) {
            percentageText.visibility = View.VISIBLE
        } else {
            percentageText.visibility = View.INVISIBLE
        }
        percentageText.text = "$progress%"
        val goUpWith = ((progress - 5).toFloat() / 100.0F) * sliderHeight
        percentageText.y = bottomY - goUpWith
    }

    private fun loadingWaitingForSocketResponse(beginRequest: Boolean) {
        if (beginRequest) {
            loadingBar.visibility = View.VISIBLE
            lightPercentageVerticalSlider?.isClickable = false
            lightsStatusContainer.children.forEach {
                it.circularButtonContainer.isClickable = false
                it.ivSettingsIcon.isClickable = false
            }

            val handler = Handler()
            handler.postDelayed({
                loadingBar.visibility = View.GONE
                lightPercentageVerticalSlider?.isClickable = true
                lightsStatusContainer.children.forEach {
                    it.circularButtonContainer.isClickable = true
                    it.ivSettingsIcon.isClickable = true
                }
            }, 10000)
        } else {
            loadingBar.visibility = View.GONE
            lightPercentageVerticalSlider?.isClickable = true
            lightsStatusContainer.children.forEach {
                it.circularButtonContainer.isClickable = true
                it.ivSettingsIcon.isClickable = true
            }
        }
    }

    private fun updateLightsUI(lightsInfoData: List<DwellingUnitDevice>?) {
        lightsInfoData?.forEach {
            val lightView: View = lightsStatusContainer.findViewWithTag(it.id)
            val statusItems = it.deviceStatus
            val statusItemsSize: Int = statusItems.size
            for (i in 0 until statusItemsSize) {
                if (statusItems[i].attributeType == SocketConstants.IOT_ATTR_TYPE_SWITCH.value) {
                    if (statusItems[i].value.startsWith(SocketConstants.IOT_ATTR_VALUE_SWITCH_ON.value)) {
                        lightView.tvItemStatus.setText(R.string.on)
                        lightView.ivItemStatusImage.setImageResource(R.drawable.lamp_on)
                        lightView.circularButtonContainer.setBackgroundResource(R.drawable.circular_opened_doors_lights)

                        var haveSettingsIcon = false
                        for (j in 0 until statusItemsSize) {
                            if (statusItems[j].attributeType == SocketConstants.IOT_ATTR_TYPE_SWITCH_LEVEL.value) {
                                lightView.ivSettingsIcon.visibility = View.VISIBLE
                                haveSettingsIcon = true
                                if (lightPercentageVerticalSlider.visibility == View.VISIBLE) {
                                    if(lightIdToChangeDimmer == it.id) {
                                        setVerticalSliderListener(true)
                                        lightPercentageVerticalSlider.progress = statusItems[j].value.toInt()
                                        changeThePercentageTextValueAndPosition(statusItems[j].value.toInt())
                                        setVerticalSliderListener(false)
                                    }
                                }
                            }
                        }
                        if (!haveSettingsIcon) {
                            lightView.ivSettingsIcon.visibility = View.INVISIBLE
                        }
                    } else {
                        lightView.tvItemStatus.setText(R.string.off)
                        lightView.ivItemStatusImage.setImageResource(R.drawable.lamp_off)
                        lightView.circularButtonContainer.setBackgroundResource(R.drawable.circular_closed_lights)
                        lightView.ivSettingsIcon.visibility = View.INVISIBLE
                    }
                    handleTheLightsScreenBackground()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as DevicesSocketService.MyDevicesServiceBinder
            devicesSocketService = binder.service
            devicesSocketBound = true

            lightsInfoData = devicesSocketService.siteDevicesData?.filter { p ->
                p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_TOGGLE.value
                        || p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_DIMMER.value
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            devicesSocketBound = false
        }
    }

    var lightIdToChangeDimmer = ""
    inner class DevicesSocketServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val deviceStatusChange = intent.getSerializableExtra("deviceLiveStatus")
            if (deviceStatusChange != null) {
                val deviceStatusModel = deviceStatusChange as DeviceFromSocket
                devicesSocketService.addOrUpdateDeviceInitialStatusItem(deviceStatusModel)
                lightsInfoData = devicesSocketService.siteDevicesData?.filter { p ->
                    p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_TOGGLE.value
                            || p.device.function == SocketConstants.IOT_FUNCTION_LIGHT_DIMMER.value
                }
                Log.d(
                    "LIGHTS_ACTIVITY",
                    "RECEIVED_STATUS_CHANGE from socket = ${deviceStatusModel.toString()}"
                )
                lightsStatusContainer.children.forEach {
                    if (deviceStatusModel.deviceID == it.tag) {
                        loadingWaitingForSocketResponse(false)
                        lightIdToChangeDimmer = deviceStatusModel.deviceID
                    }
                }
                updateLightsUI(lightsInfoData)
            }
        }
    }
}
