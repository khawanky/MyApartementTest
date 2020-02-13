package com.us.singledigits.myapartment.ui.menu.about

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.ui.menu.help.HelpActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.aboutTxt)

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
