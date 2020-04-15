package com.us.singledigits.myapartment.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.data.models.DwellingUnitDevice
import com.us.singledigits.myapartment.ui.devices.DevicesActivity
import com.us.singledigits.myapartment.ui.doors.DoorsActivity
import com.us.singledigits.myapartment.ui.fans.FansActivity
import com.us.singledigits.myapartment.ui.lights.LightsActivity
import com.us.singledigits.myapartment.ui.outlets.OutletsActivity
import com.us.singledigits.myapartment.ui.thermostat.ThermostatActivity
import com.us.singledigits.myapartment.ui.tvguide.TvguideActivity
import kotlinx.android.synthetic.main.homepage_tile_item.view.*
import java.util.*

class RecyclerHomeTilesAdapter(private var tilesList: LinkedList<HomeTileItem>) :
    RecyclerView.Adapter<RecyclerHomeTilesAdapter.HomeTileViewHolder>() {

    fun updateItems(newTilesList: LinkedList<HomeTileItem>) {
        tilesList = newTilesList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeTileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.homepage_tile_item,
            parent, false
        )

        return HomeTileViewHolder(itemView).listen { position, type ->
            val item = tilesList[position]
            val context = itemView.context
            if(item.isEnabled){
                when(item.tileName) {
                    context.getString(R.string.doorsTileName) ->  {
                            val intent = Intent(context.applicationContext, DoorsActivity::class.java)
                            intent.putExtra("doorsDevicesInfo", item.iotTileDevices as ArrayList<DwellingUnitDevice>)
                            context.startActivity(intent)
                    }
                    context.getString(R.string.lightsTileName) ->  {
                        val intent = Intent(context.applicationContext, LightsActivity::class.java)
                        intent.putExtra("lightsDevicesInfo", item.iotTileDevices as ArrayList<DwellingUnitDevice>)
                        context.startActivity(intent)
                    }
                    context.getString(R.string.thermostatsTileName) ->  {
                        val intent = Intent(context.applicationContext, ThermostatActivity::class.java)
                        intent.putExtra("thermostatsDeviceInfo", item.iotTileDevices as ArrayList<DwellingUnitDevice>)
                        context.startActivity(intent)
                    }
                    context.getString(R.string.fansTileName) ->  {
                        val intent = Intent(context.applicationContext, FansActivity::class.java)
                        intent.putExtra("fansDevicesInfo", item.iotTileDevices as ArrayList<DwellingUnitDevice>)
                        context.startActivity(intent)
                        context.startActivity(Intent(context.applicationContext, FansActivity::class.java))
                    }
                    context.getString(R.string.outletsTileName) ->  {
                        val intent = Intent(context.applicationContext, OutletsActivity::class.java)
                        intent.putExtra("outletsDevicesInfo", item.iotTileDevices as ArrayList<DwellingUnitDevice>)
                        context.startActivity(intent)
                        context.startActivity(Intent(context.applicationContext, OutletsActivity::class.java))
                    }
                    context.getString(R.string.devicesTileName) ->  {
                        context.startActivity(Intent(context.applicationContext, DevicesActivity::class.java))
                    }
                    context.getString(R.string.tvGuideTileName) ->  {
                        context.startActivity(Intent(context.applicationContext, TvguideActivity::class.java))
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: HomeTileViewHolder, position: Int) {
        val currentItem = tilesList[position]
        holder.title.text = currentItem.title
        holder.status.text = currentItem.status
        holder.status.setTextColor(currentItem.statusColor)
        holder.level.text = currentItem.level
        holder.statusIcon.setImageResource(currentItem.statusIconResource)
    }

    override fun getItemCount() = tilesList.size

    class HomeTileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tvTileTitle
        val status: TextView = itemView.tvTileStatus
        val level: TextView = itemView.tvLevelValue
        val statusIcon: ImageView = itemView.ivStatusIcon
    }

    private fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}