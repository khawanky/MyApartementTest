package com.us.singledigits.myapartment.ui.devices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.activity_public_wifi.*

class PublicWifiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_wifi)

        ivClose.setOnClickListener{
            this.finish()
        }

//        val model: DevicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
//        model.getSite()?.observe(this, Observer<Site> {
//            tvNetworkName.text = it.publicSsid
//        })
    }
}
