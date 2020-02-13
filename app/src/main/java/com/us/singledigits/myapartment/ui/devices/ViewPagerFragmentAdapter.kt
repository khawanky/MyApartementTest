package com.us.singledigits.myapartment.ui.devices

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.us.singledigits.myapartment.AlldevicesFragment
import com.us.singledigits.myapartment.MydevicesFragment

class ViewPagerFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> return MydevicesFragment()
            1 -> return AlldevicesFragment()
            else -> MydevicesFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}