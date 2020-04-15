package com.us.singledigits.myapartment.ui.notifications

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.Notification
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NotificationsActivity : BaseActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var notificationAdapter: RecyclerNotificationsListAdapter?= null
    private var notificationsItems:LinkedList<NotificationInfo> = LinkedList()
    private var notificationsViewModel: NotificationsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.notifications)

        // List
        viewManager = LinearLayoutManager(this)
        rvNotifications.layoutManager = viewManager
        rvNotifications.setHasFixedSize(true)

        //--------------- Get notifications from back-end ---------------//
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        loadSharedPreferenceData(notificationsViewModel)

        updateNotificationsList()
    }

    override fun onResume() {
        super.onResume()
        updateNotificationsList()
    }

    // This to close the app once the screen is left
//    override fun onPause() {
//        super.onPause()
//        finish()
//    }

    private fun updateNotificationsList() {
        notificationsViewModel?.getNotificationsItems(token, residentModel)?.observe(this, Observer<List<Notification>> {
            notificationsItems.clear()
            val now: Calendar = Calendar.getInstance()
            val webServiceDateFormat: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
            val dateFormat: DateFormat = SimpleDateFormat("MMM d, YYYY h:mm a", Locale.US)
            val timeFormat: DateFormat = SimpleDateFormat("h:mm a", Locale.US)

            val sentAt = Calendar.getInstance()
            var timeToView = ""
            if (it != null) {
                for (item in it) {
                    sentAt.timeInMillis =
                        webServiceDateFormat.parse(item.attributes.sentAt).time
                    if (now.get(Calendar.DATE) == sentAt.get(Calendar.DATE)) {
                        timeToView = timeFormat.format(sentAt.time).toString()
                    } else {
                        timeToView = dateFormat.format(sentAt.time).toString()
                    }

                    val isNew = item.attributes.seenAt == ""
                    val font = if (isNew) ResourcesCompat.getFont(
                        this@NotificationsActivity,
                        R.font.gibson_semi_bold
                    ) else
                        ResourcesCompat.getFont(
                            this@NotificationsActivity,
                            R.font.gibson_regular
                        )

                    notificationsItems.add(
                        NotificationInfo(
                            item.id,
                            if (item.attributes.type == "public") R.drawable.ic_building else R.drawable.ic_headphones,
                            item.attributes.type == "public", isNew, font, timeToView,
                            item.attributes.title,
                            item.attributes.content
                        )
                    )
                }

                if (notificationAdapter == null) {
                    notificationAdapter = RecyclerNotificationsListAdapter(notificationsItems)
                } else {
                    notificationAdapter?.updateItems(notificationsItems)
                }
                rvNotifications.adapter = notificationAdapter
                rvNotifications.adapter?.notifyDataSetChanged()
            }
        })
    }
}