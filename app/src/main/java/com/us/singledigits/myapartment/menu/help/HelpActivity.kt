package com.us.singledigits.myapartment.menu.help

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.help_item.*
import kotlinx.android.synthetic.main.help_item.view.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class HelpActivity : AppCompatActivity() {

    var helpItemss = ArrayList <HelpInfo>()
    var adapter:HelpAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.helpTxt)


        helpItemss.add(HelpInfo(getString(R.string.connWifiTxt) , R.drawable.closed_lamp))
        helpItemss.add(HelpInfo(getString(R.string.addGuestTxt), R.drawable.closed_lamp))
        helpItemss.add(HelpInfo(getString(R.string.addingDeviceTxt), R.drawable.tv_guide))
        helpItemss.add(HelpInfo(getString(R.string.doorsLocksTxt), R.drawable.tv_guide))
        helpItemss.add(HelpInfo(getString(R.string.lightsFansTxt), R.drawable.closed_lamp))
        helpItemss.add(HelpInfo(getString(R.string.thermostatTxt), R.drawable.tv_guide))
        helpItemss.add(HelpInfo(getString(R.string.tvGuideTxt), R.drawable.tv_guide))
        helpItemss.add(HelpInfo(getString(R.string.accountTxt), R.drawable.tv_guide))

        adapter = HelpAdapter(this, helpItemss)
        lvListHelpItems.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }


    inner class HelpAdapter: BaseAdapter {
        var context: Context?=null
        var listOfHelpItems = ArrayList<HelpInfo>()
        constructor(context: Context, helpList:ArrayList<HelpInfo>):super(){
            this.context = context
            this.listOfHelpItems = helpList
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val helpItem = listOfHelpItems[position]

            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView = inflater.inflate(R.layout.help_item, null)
            myView.tvName.text = helpItem.name!!
            myView.ivhelpIconImage.setImageResource(helpItem.image!!)
            myView.setOnClickListener {
                Toast.makeText(applicationContext, "Go to help item", Toast.LENGTH_SHORT).show()

                val intent = Intent(context, HelpInfo::class.java)
                intent.putExtra("name", helpItem.name!!)
                intent.putExtra("image", helpItem.image!!)
                context!!.startActivity(intent)

            }
            return myView
        }

        override fun getItem(position: Int): Any {
            return listOfHelpItems[position]
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
        override fun getCount(): Int {
            return listOfHelpItems.size
        }
    }
}
