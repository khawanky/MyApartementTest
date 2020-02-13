package com.us.singledigits.myapartment.ui.devices

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.us.singledigits.myapartment.AlldevicesFragment
import com.us.singledigits.myapartment.MydevicesFragment
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.DwellingUnitResident
import kotlinx.android.synthetic.main.activity_devices.*
import kotlinx.android.synthetic.main.devices_page_list_item.view.*
import kotlinx.android.synthetic.main.fragment_alldevices.*
import kotlinx.android.synthetic.main.fragment_mydevices.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*


class DevicesActivity : BaseActivity(), MydevicesFragment.OnFragmentInteractionListener,
    AlldevicesFragment.OnFragmentInteractionListener {
    var peopleItems = ArrayList<ProfileInfo>()
    var peopleAdapter: PeopleAdapter? = null

    var devicesAdapter: ViewPagerFragmentAdapter? = null
    private val tabsFragments: ArrayList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)
        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.devices)


        val model: DevicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        model.getPeopleItems()?.observe(this, Observer<List<DwellingUnitResident>> {
            if (it != null) {
                for (item in it) {
                    // TODO: Check for the real type and get the real profile image
                    peopleItems.add(
                        ProfileInfo(
                            "${item.attributes.firstName} ${item.attributes.lastName}",
                            "You", R.drawable.profilesample
                        )
                    )
                }
                peopleAdapter = PeopleAdapter(peopleItems)
                listViewPeople.adapter = peopleAdapter
            }
        })

        // Handle the tabs
        tabsFragments.add(MydevicesFragment())
        tabsFragments.add(AlldevicesFragment())
        devicesAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        viewPager2Devices.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2Devices.adapter = devicesAdapter
        viewPager2Devices.setPageTransformer(MarginPageTransformer(1500))

        TabLayoutMediator(devicesTabs, viewPager2Devices) {
            devicesTabs, position ->
                when (position) {
                    0 -> {
                        devicesTabs.text = "My Devices"
                    }
                    1 -> {
                        devicesTabs.text = "All"
                    }
                }
        }.attach()

    }

    // TODO: Not working
    fun setListeners(){
        lvAllDevices.setOnItemClickListener{ arg0, arg1, position, arg3 ->
            Toast.makeText(this,
                "arg0:$arg0, arg1:$arg1, position: $position, arg3:$arg3", Toast.LENGTH_LONG)
                .show()
        }
        lvMyDevices.setOnItemClickListener{ arg0, arg1, position, arg3 ->
            Toast.makeText(this,
                "arg0:$arg0, arg1:$arg1, position: $position, arg3:$arg3", Toast.LENGTH_LONG)
                .show()
        }
    }


    inner class PeopleAdapter(private val profileList: ArrayList<ProfileInfo>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val profileItem = profileList[position]

            val vh = if (convertView?.tag as? PeopleViewHolder == null) {
                val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val myView = inflater.inflate(R.layout.devices_page_list_item, null)

                PeopleViewHolder(profileItem, myView, myView.tvName, myView.tvType, myView.ivProfileImage).also {
                    convertView?.tag = it
                }
            } else {
                convertView.tag as PeopleViewHolder
            }

            vh.tvName.text = profileItem.name
            vh.tvType.text = profileItem.type

            vh.ivProfileImage.setImageResource(profileItem.profileImage!!)
//          TODO: Id user clicks?

//            vh.view.setOnClickListener {
//                listener?.onItemClick(profileItem)
//            }
            return vh.view
        }

        override fun getItem(position: Int): Any {
            return profileList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return profileList.size
        }

        inner class PeopleViewHolder(
            val item: ProfileInfo,
            val view: View,
            val tvName: TextView,
            val tvType: TextView,
            val ivProfileImage: ImageView
        )
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
