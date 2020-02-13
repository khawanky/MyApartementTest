package com.us.singledigits.myapartment.ui.menu.menu_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.data.models.*
import com.us.singledigits.myapartment.ui.menu.about.AboutActivity
import com.us.singledigits.myapartment.ui.menu.help.HelpActivity
import com.us.singledigits.myapartment.ui.menu.myapartment.MyapartmentActivity
import com.us.singledigits.myapartment.ui.menu.myprofile.MyprofileActivity
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val myApartment = MyApartment()
        val myProfile = MyProfile()

        val model: MenuViewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
        model.getResident()?.observe(this, Observer<Resident> {
            tvProfileName.text = it.firstName + " " + it.lastName
            myProfile.firstName = it.firstName
            myProfile.lastName = it.lastName
            myProfile.email = it.emailAddress
            myProfile.phoneNumber = it.phoneNumber
        })

        model.getSite()?.observe(this, Observer<Site> {
            myApartment.siteName = it.name
            tvLocation.text = myApartment.siteName

            myApartment.fullSiteName = it.name
            myApartment.addressPart1 = it.address
            myApartment.addressPart2 = it.city + ", " + it.state + " " + it.postalCode
            myApartment.manager = it.propertyManagerName
            myApartment.managerPhone = it.phoneNumber
            myApartment.managerEmail = it.emailAddress
        })

        model.getUnit()?.observe(this, Observer<DwellingUnit> {
            myApartment.unitString = "Unit " + it.unitLabel
            tvUnit.text = myApartment.unitString + ","
        })

        model.getRoom()?.observe(this, Observer<DwellingUnitRoom> {
            myApartment.roomString = "Room " + it.name
            tvRoom.text = myApartment.roomString
        })

        buMyProfile.setOnClickListener {
            val intent = Intent(applicationContext, MyprofileActivity::class.java)
            intent.putExtra("myProfile", myProfile)
            startActivity(intent)
        }

        buMyApartment.setOnClickListener {
            val intent = Intent(applicationContext, MyapartmentActivity::class.java)
            intent.putExtra("myApartment", myApartment)
            startActivity(intent)
        }

        buAbout.setOnClickListener {
            startActivity(Intent(applicationContext, AboutActivity::class.java))
        }

        buHelp.setOnClickListener {
            startActivity(Intent(applicationContext, HelpActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
