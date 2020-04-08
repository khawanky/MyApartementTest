package com.us.singledigits.myapartment.ui.devices

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.list_item_people.view.*
import java.util.*

class RecyclerDevicesAdapter(private val devicesList: ArrayList<DeviceInfo>) :
    RecyclerView.Adapter<RecyclerDevicesAdapter.DevicesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.devices_list_item, parent, false)
        return DevicesViewHolder(itemView).listen { position, type ->
            val item = devicesList[position]
            val context = itemView.context
            if(item.isPersonal){
                val intent = Intent(context.applicationContext, AddDeviceActivity::class.java)
                intent.putExtra("name", item.name)
                intent.putExtra("macAddress", item.macAddress)
                context.startActivity(intent)
            }
        }
    }

    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        val currentItem = devicesList[position]
        holder.tvName.text = currentItem.name
        holder.ivDeviceIcon.setImageResource(currentItem.deviceIcon!!)
    }

    override fun getItemCount() = devicesList.size

    class DevicesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tvOwnerName
        val ivDeviceIcon: ImageView = itemView.ivDeviceIcon
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}