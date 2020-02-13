package com.us.singledigits.myapartment.ui.menu.myprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.data.models.MyProfile
import kotlinx.android.synthetic.main.activity_myprofile.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class MyprofileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myprofile)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.myProfileTxt)

        val myProfile = intent.getSerializableExtra("myProfile") as? MyProfile
        etEmail.setText(myProfile?.email)
        etEmail.isEnabled = false
        etFirstName.setText(myProfile?.firstName)
        etLastName.setText(myProfile?.lastName)
        etPhone.setText(myProfile?.phoneNumber)

        // TODO: button animation
//        buSave.setOnClickListener{
//            do{
//                buSave.layoutParams.width = buSave.layoutParams.width - 96
//                buSave.width = buSave.layoutParams.width
//                buSave.requestLayout()
//
//                Thread.sleep(200)
//            } while (buSave.layoutParams.width >= 280)
//
//            buSave.setBackgroundColor(resources.getColor(R.color.greenSuccess))
//            buSave.layoutParams.width = 280
//            buSave.width = buSave.layoutParams.width
//        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
