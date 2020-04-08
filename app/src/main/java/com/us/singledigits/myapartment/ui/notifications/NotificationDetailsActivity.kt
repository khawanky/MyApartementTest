package com.us.singledigits.myapartment.ui.notifications

import android.os.Bundle
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_notification_details.*
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

        // View the help title on the toolbar and render the help topic info
        val bundle: Bundle? = intent.extras

        val isProperty = bundle?.getBoolean("isProperty")
        val title = bundle?.getString("title")
        val time = bundle?.getString("time")
        val content = bundle?.getString("content")

        if(isProperty != null && !isProperty) {
            ivNotificationIcon.setImageResource(R.drawable.ic_headphones)
            ivDeviceIcon.setBackgroundResource(0)
        }
        tvOwnerName.text = title
        tvOwner.text = time
        tvContent.text = content
    }
}
