package com.us.singledigits.myapartment.ui.devices

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.DwellingUnitAttributes
import com.us.singledigits.myapartment.ui.menu.menu_list.MenuViewModel
import kotlinx.android.synthetic.main.activity_private_wifi.*


class PrivateWifiActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_wifi)

        ivClose.setOnClickListener {
            this.finish()
        }

        var networkName = ""
        var networkPassword = ""
        var unitLabel = ""

        val model: MenuViewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        loadSharedPreferenceData(model)
        model.getDwellingUnit(token, residentModel)?.observe(this, Observer<DwellingUnitAttributes> {
            networkName =it.privateSsid
            networkPassword = it.privateSsidPassword
            unitLabel = "Unit " + it.unitLabel
            tvNetworkName.text = networkName
            tvPassword.text = networkPassword
        })

        ivEmailContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
//            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("")) // No receipt number
            val emailSubject = "$unitLabel wifi information"
            val emailBody = "Wifi SSID: $networkName \n Wifi password: $networkPassword"
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            intent.putExtra(Intent.EXTRA_TEXT, emailBody)
            try {
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    this@PrivateWifiActivity,
                    "There are no email clients installed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        ivSMSContainer.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("sms:")
            intent.type = "vnd.android-dir/mms-sms"
//            intent.putExtra("address", "") // No receipt number
            val smsBody = "Wifi SSID: $networkName \n Wifi password: $networkPassword"
            intent.putExtra("sms_body", smsBody)
            startActivity(intent)
        }
    }
}