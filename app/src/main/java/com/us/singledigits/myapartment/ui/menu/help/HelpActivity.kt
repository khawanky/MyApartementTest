package com.us.singledigits.myapartment.ui.menu.help

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.HelpTopicListItem
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.help_item.view.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class HelpActivity : BaseActivity(), OnHelpItemClickListener {
    var helpItems = ArrayList<HelpInfo>()
    var adapter: HelpAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.helpTxt)

        val model: HelpViewModel = ViewModelProviders.of(this).get(HelpViewModel::class.java)
        loadSharedPreferenceData(model)
        model.getHelpTopicItems(token, residentModel)?.observe(this, Observer<List<HelpTopicListItem>> {

            val icons = arrayOf(R.drawable.ic_add_guest, R.drawable.ic_help_wifi,R.drawable.ic_add_devices,
                                R.drawable.ic_help_tv,R.drawable.ic_help_doors_locks)
            val length = icons.size
            var count=0
            for (item in it) {
                // FIXME: the icon is static
                        helpItems.add(HelpInfo(item.helpTopicAttributes.title, item.helpTopicAttributes.content,
                            item.helpTopicAttributes.commonIssues, icons[count]))
                count++
                if(count == length) count = 0

            }
            adapter = HelpAdapter(helpItems, this)
            lvListHelpItems.adapter = adapter
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(item: HelpInfo) {
        val intent = Intent(this, HelpInfoActivity::class.java)
        intent.putExtra("title", item.title)
        intent.putExtra("commonIssues", item.commonIssues as ArrayList<String>)
        intent.putExtra("content", item.content)
        startActivity(intent)
    }

    inner class HelpAdapter(
        private val helpList: ArrayList<HelpInfo>,
        private val listener: OnHelpItemClickListener? = null
    ) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val helpItem = helpList[position]

            val vh = if (convertView?.tag as? ViewHolder == null) {
                val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val myView = inflater.inflate(R.layout.help_item, null)

                ViewHolder(helpItem, myView, myView.tvTitle, myView.ivhelpIconImage).also {
                    convertView?.tag = it
                }
            } else {
                convertView.tag as ViewHolder
            }

            vh.tvName.text = helpItem.title
            vh.ivHelpIconImage.setImageResource(helpItem.image!!)
            vh.view.setOnClickListener {
                listener?.onItemClick(helpItem)
            }
            return vh.view
        }

        override fun getItem(position: Int): Any {
            return helpList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return helpList.size
        }

        inner class ViewHolder(
            val item: HelpInfo,
            val view: View,
            val tvName: TextView,
            val ivHelpIconImage: ImageView
        )
    }
}

interface OnHelpItemClickListener {
    fun onItemClick(item: HelpInfo)
}
