package com.us.singledigits.myapartment.ui.notifications

import android.os.Bundle
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class NotificationDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.notifications)


    }
}
