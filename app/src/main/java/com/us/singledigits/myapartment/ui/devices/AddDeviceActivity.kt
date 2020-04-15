package com.us.singledigits.myapartment.ui.devices

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.DeviceRequestBody
import com.us.singledigits.myapartment.ui.menu.help.HelpActivity
import kotlinx.android.synthetic.main.activity_add_device.*
import java.util.regex.Pattern

class AddDeviceActivity : BaseActivity() {
    private lateinit var devicesViewModel: DevicesViewModel
    private lateinit var deviceRequestRequestBody:DeviceRequestBody
    private var deviceId:Int?=null
    private var accountId:String?=null
    private var macAddress:String?=null
    private var userGroupId:String?=null
    private var description:String?=null
    private var email:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        devicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        loadSharedPreferenceData(devicesViewModel)
        ivClose.setOnClickListener{
            this.finish()
        }

        // Get all needed data for the requests (Add, Update, or Delete)
        val bundle: Bundle? = intent.extras
        deviceId = bundle?.getInt("deviceId")
        accountId = bundle?.getString("accountId")
        macAddress = bundle?.getString("macAddress")
        userGroupId = bundle?.getString("userGroupId")
        description = bundle?.getString("description")
        email = residentModel?.residentAttributes?.emailAddress

        // Initial request body
        deviceRequestRequestBody = DeviceRequestBody(accountId, macAddress,userGroupId, email , description)

        if(description != null && description!!.isNotEmpty()) {
            buSave.visibility = View.INVISIBLE
            etDeviceName.setText(description)
            etMacAddress.setText(macAddress)
            etMacAddress.isEnabled = false

            etDeviceName.addTextChangedListener {
                buSave.visibility = View.VISIBLE
                if(it!=null && it.isNotEmpty()) {
                    buSave.setOnClickListener {
                        if (etDeviceName.text.trim().isEmpty() || etDeviceName == null) {
                            Toast.makeText(applicationContext, "Please enter valid device name", Toast.LENGTH_LONG).show()
                        } else {
                            deviceRequestRequestBody.description = etDeviceName.text.toString()
                            devicesViewModel.updateDevice(token, deviceId, deviceRequestRequestBody)
                        }
                    }
                }
            }
            ivDelete.setOnClickListener {
                devicesViewModel.deleteDevice(token, deviceId)
            }
        } else {
            etMacAddress.filters = arrayOf<InputFilter>(AllCaps())
            var lengthBeforeTextChanged = 0
            var lastCharBeforeTextChanged = 'x'
            etMacAddress.doOnTextChanged { text, _, _, _ ->
                if(text != null ){
                    var length = text.length
                    if (length > 0 && !Pattern.matches("^([A-F]([A-F](\\:([A-F]([A-F]" +
                                "(\\:([A-F]([A-F](\\:([A-F]([A-F](\\:([A-F]([A-F](\\:([A-F]([A-F])" +
                                "?)?)?)?)?)?)?)?)?)?)?)?)?)?)?)?)?\$", text)) {
                        etMacAddress.text.delete(length - 1, length)
                    } else {
                        if(lengthBeforeTextChanged == length+1){ // Delete char
                            if(lastCharBeforeTextChanged == ':') { // Delete another char
                                etMacAddress.text.delete(length - 1, length)
                            }
                        } else{ // Add Char
                            if(length==2 || length==5 || length==8 || length==11 || length==14){ // The next will be : append it
                                etMacAddress.text.append(":")
                                length++
                            }
                        }
                        lengthBeforeTextChanged = length
                        lastCharBeforeTextChanged = text.last()
                    }
                }
            }

            buSave.setOnClickListener{
                if(etMacAddress.text.trim().isEmpty() || etMacAddress.length() == 0
                    || etDeviceName.text.trim().isEmpty() || etDeviceName == null) {
                    Toast.makeText(applicationContext, "Please enter valid data", Toast.LENGTH_LONG).show()
                }
                else {
                    deviceRequestRequestBody.macAddress = etMacAddress.text.trim().toString()
                    deviceRequestRequestBody.description = etDeviceName.text.trim().toString()

                    val macAddressToSave = deviceRequestRequestBody.macAddress
                    if(macAddressToSave != null && macAddressToSave.matches(Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})\$"))) {
                        devicesViewModel.addDevice(token, deviceRequestRequestBody)
                    } else {
                        Toast.makeText(this@AddDeviceActivity, "Wrong mac address", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvNeedHelp.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        devicesViewModel.deviceAPIStatus?.observe(this, Observer {
            when(it){
                1 -> {
                    Toast.makeText(applicationContext, "Device ${deviceRequestRequestBody.description} added", Toast.LENGTH_LONG).show()
                    finish()
                }
                2 -> {
                    Toast.makeText(applicationContext, "Device $description updated", Toast.LENGTH_LONG).show()
                    finish()
                }
                3 -> {
                    Toast.makeText(applicationContext, "Device $description deleted", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        })
    }
//-------------- Add/Update/Delete API calls ---------------//

   /* private fun addDevice() {
        MduApi().addDevice(token, deviceRequestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called addDevice API successfully")
                    Toast.makeText(applicationContext, "Device ${deviceRequestBody.description} added", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Log.d("NOT_SUCCESS_API", "Call addDevice API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call addDevice API, Error: " + t.message)
                handleErrorCodes(20, "Error calling addDevice")
            }
        })
    }

    private fun updateDevice() {
        MduApi().updateDevice(token, deviceId, deviceRequestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called updateDevice API successfully")
                    Toast.makeText(applicationContext, "Device $description updated", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Log.d("NOT_SUCCESS_API", "Call updateDevice API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call updateDevice API, Error: " + t.message)
                handleErrorCodes(20, "Error calling updateDevice")
            }
        })
    }

    private fun deleteDevice() {
        MduApi().deleteDevice(token, deviceId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful) {
                    Log.d("SUCCESS_API", "Called deleteDevice API successfully")
                    Toast.makeText(applicationContext, "Device $description deleted", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Log.d("NOT_SUCCESS_API", "Call deleteDevice API, return with code= " + response.code())
                    handleErrorCodes(response.code(), null)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("FAILED_API", "Failed to call deleteDevice API, Error: " + t.message)
                handleErrorCodes(20, "Error calling deleteDevice")
            }
        })
    }*/

}
