package com.us.singledigits.myapartment.ui.devices

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.list_item_people.view.*
import java.util.*

class RecyclerPeopleAdapter(private val peopleList: ArrayList<ProfileInfo>) :
    RecyclerView.Adapter<RecyclerPeopleAdapter.PeopleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_people, parent, false)
        return PeopleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val currentItem = peopleList[position]
        holder.title.text = currentItem.name
        holder.owner.text = currentItem.type
    }

    override fun getItemCount() = peopleList.size

    class PeopleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.tvTitle
        val owner: TextView = itemView.tvTime
    }
}