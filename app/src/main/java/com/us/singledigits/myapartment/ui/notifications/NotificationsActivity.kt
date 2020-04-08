package com.us.singledigits.myapartment.ui.notifications

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.Notification
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.list_item_notification.view.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NotificationsActivity : BaseActivity(), OnNotificationItemClickListener {
    var notificationsItems = ArrayList<NotificationInfo>()
    var notificationAdapter: NotificationAdapter? = null
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
        loadSharedPreferenceData()

        //--------------- Get notifications from back-end ---------------//
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        notificationsViewModel?.getNotificationsItems(token, residentModel)?.observe(this, Observer<List<Notification>> {
            val now: Calendar = Calendar.getInstance()
            val webServiceDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
            val dateFormat: DateFormat = SimpleDateFormat("MMM d, YYYY h:mm a", Locale.US)
            val timeFormat: DateFormat = SimpleDateFormat("h:mm a", Locale.US)

            val sentAt = Calendar.getInstance()
            var timeToView = ""
            if (it != null) {
                for (item in it) {
                    sentAt.timeInMillis = webServiceDateFormat.parse(item.attributes.sentAt).time

                    if (now.get(Calendar.DATE) == sentAt.get(Calendar.DATE) ) {
                        timeToView = timeFormat.format(sentAt.time).toString()
                    } else {
                        timeToView = dateFormat.format(sentAt.time).toString()
                    }

                    notificationsItems.add(
                        NotificationInfo(
                            item.id,
                            if(item.attributes.type == "public") R.drawable.ic_building else R.drawable.ic_headphones,
                            item.attributes.type == "public",
                            item.attributes.seenAt == "",
                            timeToView,
                            item.attributes.title,
                            item.attributes.content
                        )
                    )
                }
                notificationAdapter = NotificationAdapter(notificationsItems, this)
                listViewNotifications.adapter = notificationAdapter
            }
        })
    }

    override fun onItemClick(item: NotificationInfo) {
        val intent = Intent(this, NotificationDetailsActivity::class.java)
        intent.putExtra("isProperty", item.isProperty)
        intent.putExtra("title", item.title)
        intent.putExtra("time", item.time)
        intent.putExtra("content", item.content)

        val now: Calendar = Calendar.getInstance()
        val webServiceDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        val stringNowDate = webServiceDateFormat.format(now.time).toString()
        val jsonPayload = JSONObject()
        jsonPayload.put("seenAt", stringNowDate)

        if(item.id != null) {
            notificationsViewModel?.sendNotificationAcknowledgement(token, item.id!!, jsonPayload.toString())
        }

        startActivity(intent)
    }

    inner class NotificationAdapter(private val notificationsList: ArrayList<NotificationInfo>,
                                    private val listener: OnNotificationItemClickListener? = null) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val notificationItem = notificationsList[position]
            val vh = if (convertView?.tag as? NotificationViewHolder == null) {
                val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val myView = inflater.inflate(R.layout.list_item_notification, null)

                NotificationViewHolder(
                    notificationItem,
                    myView,
                    myView.ivNotificationIcon,
                    myView.tvOwnerName,
                    myView.tvOwner,
                    myView.ivDeviceIcon,
                    myView.ivNotReadNotification
                ).also {
                    convertView?.tag = it
                }
            } else {
                convertView.tag as NotificationViewHolder
            }

            if(notificationItem.isProperty !=null && !notificationItem.isProperty!!){
                vh.iconContainer.setBackgroundResource(0)
            }

            vh.ivNotificationIcon.setImageResource(notificationItem.icon!!)
            vh.tvTitle.text = notificationItem.title
            vh.tvTime.text = notificationItem.time

            if(notificationItem.isNew !=null && notificationItem.isNew!!) {
                vh.tvTitle.typeface = ResourcesCompat.getFont(this@NotificationsActivity, R.font.gibson_semi_bold)
                vh.tvTime.typeface = ResourcesCompat.getFont(this@NotificationsActivity, R.font.gibson_semi_bold)
            } else {
                vh.ivReadRedDot.setBackgroundResource(0)
            }

            vh.view.setOnClickListener {
                listener?.onItemClick(notificationItem)
            }
            return vh.view
        }

        override fun getItem(position: Int): Any {
            return notificationsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return notificationsList.size
        }

        inner class NotificationViewHolder(
            val item: NotificationInfo,
            val view: View,
            val ivNotificationIcon: ImageView,
            val tvTitle: TextView,
            val tvTime: TextView,
            val iconContainer: ConstraintLayout,
            val ivReadRedDot: ImageView

        )
    }
}

interface OnNotificationItemClickListener {
    fun onItemClick(item: NotificationInfo)
}