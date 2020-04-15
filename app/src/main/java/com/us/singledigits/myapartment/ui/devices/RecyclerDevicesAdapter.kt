package com.us.singledigits.myapartment.ui.devices

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.devices_list_item.view.*
import java.util.*

class RecyclerDevicesAdapter(private var devicesList: ArrayList<DeviceInfo>) :
    RecyclerView.Adapter<RecyclerDevicesAdapter.DevicesViewHolder>() {

    private var accountId:String?=null
    private var userGroupId:String?=null

    fun updateItems(newDevicesList: ArrayList<DeviceInfo>) {
        devicesList = newDevicesList
    }
    fun setAccountId(accountId: String) {
        this.accountId = accountId
    }
    fun setUserGroupId(userGroupId: String) {
        this.userGroupId = userGroupId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.devices_list_item, parent, false)
        return DevicesViewHolder(itemView).listen { position, type ->
            val item = devicesList[position]
            val context = itemView.context
            if(item.isPersonal){
                val intent = Intent(context.applicationContext, AddDeviceActivity::class.java)
                intent.putExtra("deviceId", item.deviceId)
                intent.putExtra("accountId", accountId)
                intent.putExtra("macAddress", item.macAddress)
                intent.putExtra("userGroupId", userGroupId)
                intent.putExtra("description", item.name)

                if(!accountId.isNullOrEmpty() && !userGroupId.isNullOrEmpty()) {
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        val currentItem = devicesList[position]
        holder.deviceName.text = currentItem.name
        holder.deviceIcon.setImageResource(currentItem.deviceIcon)
    }

    override fun getItemCount() = devicesList.size

    class DevicesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.tvTitle
        val deviceIcon: ImageView = itemView.ivNotIconContainer
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}