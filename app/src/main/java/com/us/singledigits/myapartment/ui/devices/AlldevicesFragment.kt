package com.us.singledigits.myapartment.ui.devices

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.StaticConstants
import com.us.singledigits.myapartment.data.models.Device
import com.us.singledigits.myapartment.data.network.responses.ResidentResponse
import kotlinx.android.synthetic.main.devices_list_item.view.*
import kotlinx.android.synthetic.main.fragment_alldevices.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AlldevicesFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AlldevicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AlldevicesFragment : Fragment(),
    OnAllDeviceItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    var devicesItems = ArrayList<DeviceInfo>()
    var devicesAdapter: DevicesAdapter? = null
    private var token:String? = null
    private var residentModel: ResidentResponse? =  null
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        token = StaticConstants.getSharedPreferencesConfig(this.activity)?.getToken()
        val jsonResident = StaticConstants.getSharedPreferencesConfig(this.activity)?.getResident()
        residentModel = gson.fromJson(jsonResident, ResidentResponse::class.java)

        val model: DevicesViewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        model.getAllDevicesItems(token, residentModel)?.observe(viewLifecycleOwner, Observer<List<Device>> {
            if (it != null) {
                for (item in it) {
                    // TODO: Check for the real status and get the real device icon
                    devicesItems.add(DeviceInfo(item.attributes.name,
                        "Unknown", item.attributes.macAddress,
                        R.drawable.computer , item.isPersonal
                    ))
                }
                devicesAdapter = DevicesAdapter(devicesItems, this)
                lvAllDevices.adapter = devicesAdapter
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alldevices, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AlldevicesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlldevicesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(item: DeviceInfo) {
        val intent = Intent(activity, AddDeviceActivity::class.java)
        intent.putExtra("name", item.name)
        intent.putExtra("macAddress", item.macAddress)
        startActivity(intent)
    }

    inner class DevicesAdapter(
        private val devicesList: ArrayList<DeviceInfo>,
        private val listener: OnAllDeviceItemClickListener? = null
    ) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val deviceItem = devicesList[position]

            val vh = if (convertView?.tag as? DeviceViewHolder == null) {
                val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val myView = inflater.inflate(R.layout.devices_list_item, null)

                DeviceViewHolder(deviceItem, myView, myView.tvOwnerName, myView.ivDeviceIcon).also {
                    convertView?.tag = it
                }
            } else {
                convertView.tag as DeviceViewHolder
            }

            vh.tvName.text = deviceItem.name
            vh.ivProfileImage.setImageResource(deviceItem.deviceIcon!!)

            if(deviceItem.isPersonal) {
                vh.view.setOnClickListener {
                    listener?.onItemClick(deviceItem)
                }
            } else {
                vh.view.setOnClickListener(null)
            }
            return vh.view
        }

        override fun getItem(position: Int): Any {
            return devicesList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return devicesList.size
        }

        inner class DeviceViewHolder(
            val item: DeviceInfo,
            val view: View,
            val tvName: TextView,
            val ivProfileImage: ImageView
        )
    }
}

interface OnAllDeviceItemClickListener {
    fun onItemClick(item: DeviceInfo)
}
