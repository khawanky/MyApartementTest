package com.us.singledigits.myapartment.menu.menu_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.menu.about.AboutActivity
import com.us.singledigits.myapartment.menu.help.HelpActivity
import com.us.singledigits.myapartment.menu.myapartment.MyapartmentActivity
import com.us.singledigits.myapartment.menu.myprofile.MyprofileActivity
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        buMyProfile.setOnClickListener{
            startActivity(Intent(applicationContext, MyprofileActivity::class.java))
        }

        buMyApartment.setOnClickListener{
            startActivity(Intent(applicationContext, MyapartmentActivity::class.java))
        }

        buAbout.setOnClickListener{
            startActivity(Intent(applicationContext, AboutActivity::class.java))
        }

        buHelp.setOnClickListener{
            startActivity(Intent(applicationContext, HelpActivity::class.java))
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
