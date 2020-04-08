package com.us.singledigits.myapartment.ui.doors

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.SocketConstants
import com.us.singledigits.myapartment.data.models.DeviceFromSocket
import com.us.singledigits.myapartment.data.models.DwellingUnitDevice
import com.us.singledigits.myapartment.data.models.DwellingUnitDeviceAttributes
import com.us.singledigits.myapartment.data.services.DevicesSocketService
import com.us.singledigits.myapartment.ui.menu.help.HelpActivity
import kotlinx.android.synthetic.main.activity_doors.*
import kotlinx.android.synthetic.main.doors_widget_button.view.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*
import org.json.JSONObject


class DoorsActivity : AppCompatActivity() {
    private val deviceStatusReceiver = DevicesSocketServiceReceiver()
    private lateinit var devicesSocketService: DevicesSocketService
    private var devicesSocketBound = false
    private var doorsInfoData: List<DwellingUnitDevice>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doors)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.doors)

        tvNeedHelp.setOnClickListener {
            this.startActivity(Intent(this, HelpActivity::class.java))
        }

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val doorsDevicesInfo =
            intent.extras?.getSerializable("doorsDevicesInfo") as ArrayList<DwellingUnitDevice>

        if(doorsDevicesInfo.size == 1) {
            toolbar_title.setText(R.string.door)
        }

        doorsDevicesInfo.forEach {
            val doorView: View = inflater.inflate(R.layout.doors_widget_button, null)
            doorView.tvItemRoom.text = it.device.name
            doorView.tag = it.device.platformIdentifier

            val deviceAttributes = it.device
            val statusItems = it.deviceStatus
            val statusItemsSize: Int = statusItems.size
            for (i in 0 until statusItemsSize) {
                if (statusItems[i].attributeType == SocketConstants.IOT_ATTR_TYPE_LOCK.value) {
                    if (statusItems[i].value.startsWith(SocketConstants.IOT_ATTR_VALUE_LOCK_LOCKED.value)) {
                        doorView.tvItemStatus.setText(R.string.locked)
                        doorView.ivItemStatusImage.setImageResource(R.drawable.door_locked)
                        doorView.circularButtonContainer.setBackgroundResource(R.drawable.circular_closed_doors)
                    } else {
                        doorView.tvItemStatus.setText(R.string.unlocked)
                        doorView.ivItemStatusImage.setImageResource(R.drawable.door_unlocked)
                        doorView.circularButtonContainer.setBackgroundResource(R.drawable.circular_opened_doors_lights)
                    }
                }
            }
            doorView.setOnClickListener {
                doorClickListener(deviceAttributes, doorView)
            }
            doorsStatusContainer.addView(doorView)
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(deviceStatusReceiver, IntentFilter("listenToDevicesStatus"))
    }

    override fun onStart() {
        super.onStart()
        Intent(this, DevicesSocketService::class.java).also { intent ->
            bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun loadingWaitingForSocketResponse(beginRequest: Boolean) {
        if (beginRequest) {
            loadingBar.visibility = View.VISIBLE
            doorsStatusContainer.children.forEach {
                it.circularButtonContainer.isEnabled = false
            }

            val handler = Handler()
            handler.postDelayed({
                loadingBar.visibility = View.GONE
                doorsStatusContainer.children.forEach {
                    it.circularButtonContainer.isEnabled = true
                }
            }, 10000)
        } else {
            loadingBar.visibility = View.GONE
            doorsStatusContainer.children.forEach {
                it.circularButtonContainer.isEnabled = true
            }
        }
    }

    private fun doorClickListener(deviceAttributes: DwellingUnitDeviceAttributes, doorView: View) {
        val jsonPayload = JSONObject()
        jsonPayload.put(SocketConstants.KEY_COMMAND.value, SocketConstants.SWITCHES_COMMAND.value)
        jsonPayload.put(
            SocketConstants.KEY_DEVICE_TYPE.value,
            SocketConstants.DOORS_TOGGLE_DEVICE_TYPE.value
        )
        if (doorView.tvItemStatus.text == getString(R.string.unlocked)) {
            jsonPayload.put(
                SocketConstants.KEY_ACTION.value,
                SocketConstants.DOORS_TOGGLE_ACTION_LOCK.value
            )
        } else {
            jsonPayload.put(
                SocketConstants.KEY_ACTION.value,
                SocketConstants.DOORS_TOGGLE_ACTION_UNLOCK.value
            )
        }
        loadingWaitingForSocketResponse(true)
        devicesSocketService.changeDeviceStatus(deviceAttributes, jsonPayload)
    }

    private fun updateDoorsUI(doorsInfoData: List<DwellingUnitDevice>?) {
        doorsInfoData?.forEach {
            val doorView: View = doorsStatusContainer.findViewWithTag(it.id)
            val statusItems = it.deviceStatus
            val statusItemsSize: Int = statusItems.size
            for (i in 0 until statusItemsSize) {
                if (statusItems[i].attributeType == SocketConstants.IOT_ATTR_TYPE_LOCK.value) {
                    if (statusItems[i].value.startsWith(SocketConstants.IOT_ATTR_VALUE_LOCK_LOCKED.value)) {
                        doorView.tvItemStatus.setText(R.string.locked)
                        doorView.ivItemStatusImage.setImageResource(R.drawable.door_locked)
                        doorView.circularButtonContainer.setBackgroundResource(R.drawable.circular_closed_doors)
                    } else {
                        doorView.tvItemStatus.setText(R.string.unlocked)
                        doorView.ivItemStatusImage.setImageResource(R.drawable.door_unlocked)
                        doorView.circularButtonContainer.setBackgroundResource(R.drawable.circular_opened_doors_lights)
                    }
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
                doorsInfoData =
                    devicesSocketService.siteDevicesData?.filter { p -> p.device.function == SocketConstants.IOT_FUNCTION_LOCK.value }
                Log.d(
                    "DOORS_ACTIVITY",
                    "RECEIVED_STATUS_CHANGE from socket = ${deviceStatusModel.toString()}"
                )

                doorsStatusContainer.children.forEach {
                    if (deviceStatusModel.deviceID == it.tag) {
                        updateDoorsUI(doorsInfoData)
                        loadingWaitingForSocketResponse(false)
                    }
                }
            }
        }
    }
}
