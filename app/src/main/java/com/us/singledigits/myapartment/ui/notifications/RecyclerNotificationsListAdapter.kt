package com.us.singledigits.myapartment.ui.notifications

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.list_item_notification.view.*
import java.util.*

class RecyclerNotificationsListAdapter(private var notificationsList: LinkedList<NotificationInfo>) :
    RecyclerView.Adapter<RecyclerNotificationsListAdapter.NotificationItemViewHolder>() {

    fun updateItems(newNotificationsList: LinkedList<NotificationInfo>) {
        notificationsList = newNotificationsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
                    R.layout.list_item_notification, parent, false)

        return NotificationItemViewHolder(itemView).listen { position, type ->
            val item = notificationsList[position]
            val context = itemView.context

            val intent = Intent(context.applicationContext, NotificationDetailsActivity::class.java)
            intent.putExtra("notificationId", item.id)
            intent.putExtra("isProperty", item.isProperty)
            intent.putExtra("title", item.title)
            intent.putExtra("time", item.time)
            intent.putExtra("content", item.content)
            intent.putExtra("isNew", item.isNew)
            context.startActivity(intent)
        }
    }

    override fun onBindViewHolder(holder: NotificationItemViewHolder, position: Int) {
        val currentItem = notificationsList[position]
        holder.ivNotificationIcon.setImageResource(currentItem.icon)
        holder.tvTitle.text = currentItem.title
        holder.tvTime.text = currentItem.time

        holder.tvTitle.typeface = currentItem.font
        holder.tvTime.typeface = currentItem.font

        if(currentItem.isProperty) {
            holder.iconContainer.setBackgroundResource(R.drawable.circular_notification_icon)
        } else {
            holder.iconContainer.setBackgroundResource(0)
        }

        if(currentItem.isNew) {
            holder.ivReadRedDot.setBackgroundResource(R.drawable.circular_notification_not_read)
        } else {
            holder.ivReadRedDot.setBackgroundResource(0)
        }
    }

    override fun getItemCount() = notificationsList.size

    class NotificationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivNotificationIcon: ImageView = itemView.ivNotificationIcon
        val tvTitle: TextView = itemView.tvTitle
        val tvTime: TextView = itemView.tvTime
        val iconContainer: ConstraintLayout = itemView.ivNotIconContainer
        val ivReadRedDot: ImageView = itemView.ivNotReadNotification
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}