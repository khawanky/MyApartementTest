package com.us.singledigits.myapartment.ui.menu.myapartment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.data.models.MyApartment
import com.us.singledigits.myapartment.ui.menu.help.HelpActivity
import kotlinx.android.synthetic.main.activity_about.tvNeedHelp
import kotlinx.android.synthetic.main.activity_myapartment.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*


class MyapartmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myapartment)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.myApartmentTxt)

        val myApartment = intent.getSerializableExtra("myApartment") as? MyApartment


        tvHotelName.text = myApartment?.siteName
        tvUnit.text = myApartment?.unitString
        tvRoom.text = myApartment?.roomString
        tvHotelFullName.text = myApartment?.siteName
        tvAddressPart1.text = myApartment?.addressPart1
        tvAddressPart2.text = myApartment?.addressPart2
        tvPropertyManager.text = myApartment?.manager

        // TODO: Get real office hours later
        val n = 10
        for (i in 1 until n) {
            val rowTextView = TextView(this)
            rowTextView.text = "Date $i test dummy"

            var params : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // This will define text view width
                LinearLayout.LayoutParams.WRAP_CONTENT // This will define text view height
            )
            rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16F)
            rowTextView.setTextColor(Color.parseColor("#505050"))

            rowTextView.typeface = ResourcesCompat.getFont(this, R.font.gibson_light_regular)
            rowTextView.layoutParams = params

            officeHoursLinearLayout.addView(rowTextView)
        }

        tvNeedHelp.setOnClickListener {
            this.startActivity(Intent(this, HelpActivity::class.java))
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
