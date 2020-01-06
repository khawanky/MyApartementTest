package com.us.singledigits.myapartment.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.menu.menu_list.MenuActivity
import kotlinx.android.synthetic.main.activity_home_page.*

class HomePageActivity : AppCompatActivity() {

    var sections = ArrayList <Section>()
    var sectionAdapter:SectionAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        hamburger.setOnClickListener{
            startActivity(Intent(applicationContext, MenuActivity::class.java))
        }

        sections.add(Section("Doors", "ALL LOCKED", R.drawable.locked))
        sections.add(Section("Lights", "1 ON", R.drawable.closed_lamp))
        sections.add(Section("Thermostat", "HEATING", R.drawable.closed_lamp))
        sections.add(Section("Devices", "3 Total", R.drawable.tv_guide))
        sections.add(Section("TV guide", "", R.drawable.tv_guide))

        sectionAdapter = SectionAdapter(this, sections)
        apartment_sections.adapter = sectionAdapter
        apartment_sections.isExpanded = true
    }
}

