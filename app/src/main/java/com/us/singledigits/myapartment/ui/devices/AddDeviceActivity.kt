package com.us.singledigits.myapartment.ui.devices

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.ui.menu.help.HelpActivity
import kotlinx.android.synthetic.main.activity_add_device.*

class AddDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)
        ivClose.setOnClickListener{
            this.finish()
        }

        tvNeedHelp.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        val bundle: Bundle? = intent.extras
        val name = bundle?.getString("name")
        val macAddress = bundle?.getString("macAddress")

        if(name == null || name.isEmpty()) {
            buSave.setOnClickListener{
                if(etMacAddress.text.trim().isEmpty() || etMacAddress.length() == 0
                        || etDeviceName.text.trim().isEmpty() || etDeviceName == null) {
                    Toast.makeText(applicationContext, "Please enter valid data", Toast.LENGTH_LONG).show()
                }
                else {
                    // TODO: Call back end here
                    startActivity(Intent(this, DevicesActivity::class.java))
                }
            }
        } else {
            buSave.visibility = View.INVISIBLE
            etDeviceName.setText(name)
            etMacAddress.setText(macAddress)
            etDeviceName.isEnabled = false
            etMacAddress.isEnabled = false
        }
    }
}
