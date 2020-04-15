package com.us.singledigits.myapartment.ui.devices

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.us.singledigits.myapartment.commons.ui.BaseViewModel
import com.us.singledigits.myapartment.data.models.DwellingUnitAttributes
import com.us.singledigits.myapartment.data.models.DwellingUnitResident
import kotlinx.android.synthetic.main.activity_devices.*
import kotlinx.android.synthetic.main.activity_devices.view.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class DevicesActivity : BaseActivity() {
    var peopleItems = ArrayList<ProfileInfo>()
    private var accountId:String ?= null
    private var userGroupId: String? = null
    private var privateWifiNetwork:String ?= null
    private var privateWifiPassword: String? = null
    private var unitLabel: String? = null
    private lateinit var devicesViewModel: DevicesViewModel
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

        // List
        viewManager = LinearLayoutManager(this)
        rvPeople.layoutManager = viewManager
        rvPeople.setHasFixedSize(true)

//--------------- Set some onClick listeners ---------------//
        ivAddIcon.setOnClickListener {
            val intent = Intent(this, AddDeviceActivity::class.java)
            intent.putExtra("accountId", accountId)
            intent.putExtra("userGroupId", userGroupId)
            startActivity(intent)
        }

        buPrivateWifi.setOnClickListener {
            val intent = Intent(this, PrivateWifiActivity::class.java)
            intent.putExtra("privateWifiNetwork", privateWifiNetwork)
            intent.putExtra("privateWifiPassword", privateWifiPassword)
            intent.putExtra("unitLabel", "Unit $unitLabel")
//            if(privateWifiNetwork.isNullOrEmpty() || privateWifiPassword.isNullOrEmpty()) {
//                Toast.makeText(this@DevicesActivity, "No private wifi details available", Toast.LENGTH_LONG).show()
//            } else {
                startActivity(intent)
//            }
        }

        // Button visibility is Gone currently
        buPublicWifi.setOnClickListener {
            startActivity(Intent(this, PublicWifiActivity::class.java))
        }

//--------------- Get people details from back-endذ ---------------//
        devicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        loadSharedPreferenceData(devicesViewModel)
        devicesViewModel.getPeopleItems(token, residentModel)?.observe(this, Observer<List<DwellingUnitResident>> {
            if (it != null) {
                Log.d("DEVICES_ACTIVITY", "getPeopleItems size: ${it.size}")
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

        loadTabsCounts()

        devicesViewModel.getDwellingUnitAttributes()?.observe(this, Observer<DwellingUnitAttributes> {
            Log.d("DEVICES_ACTIVITY", "getDwellingUnitAttributes: $it")
            privateWifiNetwork = it.privateSsid
            privateWifiPassword = it.privateSsidPassword
            unitLabel = it.unitLabel
        })

        ivAddIcon.isEnabled = false
        devicesViewModel.getAccountId()?.observe(this, Observer {
            if(it != null) {
                Log.d("DEVICES_ACTIVITY", "getAccountId: $it")
                accountId = it
                if (!userGroupId.isNullOrEmpty() && !accountId.isNullOrEmpty()) {
                    ivAddIcon.isEnabled = true
                }
            }
        })
        devicesViewModel.getUserGroupId()?.observe(this, Observer {
            if(it != null) {
                Log.d("DEVICES_ACTIVITY", "getUserGroupId: $it")
                userGroupId = it
                if (!userGroupId.isNullOrEmpty() && !accountId.isNullOrEmpty()) {
                    ivAddIcon.isEnabled = true
                }
            }
        })

//------------- Handle the tabs -------------//
        // add tabs Fragments
        tabsFragments.add(MydevicesFragment.newInstance())
        tabsFragments.add(AlldevicesFragment.newInstance())

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

    override fun onResume() {
        super.onResume()
        viewPager2Devices.let {
            viewPager2Devices.adapter.let {
                tabsFragments.add(MydevicesFragment.newInstance())
                tabsFragments.add(AlldevicesFragment.newInstance())
                viewPager2Devices.adapter = devicesAdapter
                it?.notifyDataSetChanged()
            }
        }
        loadTabsCounts()
    }

    private fun loadTabsCounts(){
        devicesViewModel.getMyDevicesItemsCount(token, residentModel)?.observe(this, Observer<Int> {
            if (it != null) {
                Log.d("DEVICES_ACTIVITY", "getMyDevicesItemsCount size: $it")
                linearLayoutTabs.devicesTabs.getTabAt(0)?.text = "My Devices ($it)"
            }
        })

        devicesViewModel.getAllDevicesItemsCount(token, residentModel)?.observe(this, Observer<Int> {
            if (it != null) {
                Log.d("DEVICES_ACTIVITY", "getAllDevicesItemsCount size: $it")
                linearLayoutTabs.devicesTabs.getTabAt(1)?.text = "All ($it)"
            }
        })
    }
}
