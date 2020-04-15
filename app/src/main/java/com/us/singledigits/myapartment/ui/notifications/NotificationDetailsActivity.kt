package com.us.singledigits.myapartment.ui.notifications

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.NotificationAckRequestBody
import kotlinx.android.synthetic.main.activity_notification_details.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NotificationDetailsActivity : BaseActivity() {
    private var notificationsViewModel: NotificationsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.notifications)

        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        loadSharedPreferenceData(notificationsViewModel)

        // View the help title on the toolbar and render the help topic info
        val bundle: Bundle? = intent.extras
        val notificationId = bundle?.getInt("notificationId")
        val isProperty = bundle?.getBoolean("isProperty")
        val title = bundle?.getString("title")
        val time = bundle?.getString("time")
        val content = bundle?.getString("content")

        val isNew = bundle?.getBoolean("isNew")

        if(isProperty != null && !isProperty) {
            ivNotificationIcon.setImageResource(R.drawable.ic_headphones)
            ivNotIconContainer.setBackgroundResource(0)
        }
        tvTitle.text = title
        tvTime.text = time
        tvContent.text = content

        if(notificationId != null && isNew!=null && isNew) {
            val now: Calendar = Calendar.getInstance()
            val webServiceDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
            val stringNowDate = webServiceDateFormat.format(now.time).toString()
            val ackRequestBody = NotificationAckRequestBody(stringNowDate)
            notificationsViewModel?.sendNotificationAcknowledgement(token, notificationId, ackRequestBody)
        }

    }
}
