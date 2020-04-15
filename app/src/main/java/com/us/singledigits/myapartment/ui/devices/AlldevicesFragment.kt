package com.us.singledigits.myapartment.ui.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.StaticConstants
import com.us.singledigits.myapartment.data.models.Device
import com.us.singledigits.myapartment.data.network.responses.ResidentResponse
import kotlinx.android.synthetic.main.fragment_alldevices.*

class AlldevicesFragment : Fragment() {
    private var devicesItems = ArrayList<DeviceInfo>()
    private var devicesAdapter: RecyclerDevicesAdapter = RecyclerDevicesAdapter(ArrayList())
    private var token:String? = null
    private var residentModel: ResidentResponse? =  null
    private val gson = Gson()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val viewModel = ViewModelProviders.of(activity!!).get<DevicesViewModel>(DevicesViewModel::class.java)
        viewModel.getAccountId()?.observe(activity!!, Observer {
            if(!it.isNullOrEmpty())
                devicesAdapter.setAccountId(it)
        })
        viewModel.getUserGroupId()?.observe(activity!!, Observer {
            if(!it.isNullOrEmpty())
                devicesAdapter.setUserGroupId(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alldevices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        token = StaticConstants.getSharedPreferencesConfig(this.activity)?.getToken()
        val jsonResident = StaticConstants.getSharedPreferencesConfig(this.activity)?.getResident()
        residentModel = gson.fromJson(jsonResident, ResidentResponse::class.java)
        rvAllDevices.layoutManager = LinearLayoutManager(context)
        rvAllDevices.setHasFixedSize(true)

        val model: DevicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        model.getAllDevicesItems(token, residentModel)?.observe(viewLifecycleOwner, Observer<List<Device>> {
            if (it != null) {
                for (item in it) {
                    // TODO: Check for the real status and get the real device icon
                    devicesItems.add(DeviceInfo(item.id , item.attributes.name,
                        "Unknown", item.attributes.macAddress,
                        R.drawable.computer , item.isPersonal
                    ))
                }
                devicesAdapter.updateItems(devicesItems)
                rvAllDevices.adapter = devicesAdapter
                rvAllDevices.adapter?.notifyDataSetChanged()
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }
    companion object {
        @JvmStatic
        fun newInstance() = AlldevicesFragment()
    }
}
