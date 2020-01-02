package com.us.singledigits.myapartment.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.section_tile.view.*

class SectionAdapter: BaseAdapter {
    var context: Context? = null
    var listOfSections = ArrayList<Section>()
    var view:View?=null

    constructor(context: Context, listOfSections: ArrayList<Section>) : super() {
        this.context = context
        this.listOfSections = listOfSections
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null){
            view = View(context)
            view = inflater.inflate(R.layout.section_tile, null)
            val section = listOfSections[position]

            view!!.tvTitle.text = section.title!!
            view!!.tvStatus.text = section.status!!
            view!!.ivStatusIcon.setImageResource(section.statusIcon!!)
        }




        view!!.setOnClickListener {
            Toast.makeText(context, "Go to the proper page", Toast.LENGTH_SHORT).show()
            // TODO: handle the navigation
//            val intent = Intent(context, FoodDetails::class.java)
//            intent.putExtra("name", food.name!!)
//            intent.putExtra("des", food.des!!)
//            intent.putExtra("image", food.image!!)
//            context!!.startActivity(intent)
        }
        return view!!
    }

    override fun getItem(position: Int): Any {
        return listOfSections[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listOfSections.size
    }


}