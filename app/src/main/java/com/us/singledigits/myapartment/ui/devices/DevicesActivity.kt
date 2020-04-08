package com.us.singledigits.myapartment.ui.devices

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import com.us.singledigits.myapartment.data.models.DwellingUnitResident
import kotlinx.android.synthetic.main.activity_devices.*
import kotlinx.android.synthetic.main.activity_devices.view.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class DevicesActivity : BaseActivity(), MydevicesFragment.OnFragmentInteractionListener,
    AlldevicesFragment.OnFragmentInteractionListener {

    var peopleItems = ArrayList<ProfileInfo>()
    var devicesAdapter: ViewPagerFragmentAdapter? = null

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var peopleAdapter: RecyclerPeopleAdapter

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
        loadSharedPreferenceData()

        // List
        viewManager = LinearLayoutManager(this)
        rvPeople.layoutManager = viewManager
        rvPeople.setHasFixedSize(true)

//--------------- Set some onClick listeners ---------------//
        ivAddIcon.setOnClickListener {
            startActivity(Intent(this, AddDeviceActivity::class.java))
        }
        buPrivateWifi.setOnClickListener {
            startActivity(Intent(this, PrivateWifiActivity::class.java))
        }
        // Button visibility is Gone currently
        buPublicWifi.setOnClickListener {
            startActivity(Intent(this, PublicWifiActivity::class.java))
        }

//--------------- Get people details from back-end ---------------//
        val model: DevicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        model.getPeopleItems(token, residentModel)?.observe(this, Observer<List<DwellingUnitResident>> {
            if (it != null) {
                tvPeople.text = getString(R.string.people) + " (" + it.size + ")"
                for (item in it) {
                    // TODO: Check for the real type and get the real profile image
                    peopleItems.add(ProfileInfo("${item.attributes.firstName} ${item.attributes.lastName}",
                            if(item.id == residentModel?.id) "You" else "Roommate" , R.drawable.profilesample))
                }
                peopleAdapter = RecyclerPeopleAdapter(peopleItems)
                rvPeople.adapter = peopleAdapter
            }
        })

        model.getMyDevicesItemsCount(token, residentModel)?.observe(this, Observer<Int> {
            if (it != null) {
                linearLayoutTabs.devicesTabs.getTabAt(0)?.text = "My Devices ($it)"
            }
        })

        model.getAllDevicesItemsCount(token, residentModel)?.observe(this, Observer<Int> {
            if (it != null) {
                linearLayoutTabs.devicesTabs.getTabAt(1)?.text = "All ($it)"
            }
        })

//------------- Handle the tabs -------------//
        tabsFragments.add(MydevicesFragment())
        tabsFragments.add(AlldevicesFragment())
        devicesAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        viewPager2Devices.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager2Devices.adapter = devicesAdapter
        viewPager2Devices.setPageTransformer(MarginPageTransformer(1000))
        TabLayoutMediator(devicesTabs, viewPager2Devices) { devicesTabs, position ->
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

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not used now") //To change body of created functions use File | Settings | File Templates.
    }
}
