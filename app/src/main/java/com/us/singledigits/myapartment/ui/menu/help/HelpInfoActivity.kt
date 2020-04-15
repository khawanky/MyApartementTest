package com.us.singledigits.myapartment.ui.menu.help

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginBottom
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.activity_help_info.*
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class HelpInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_info)

        // Action barl
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // View the help title on the toolbar and render the help topic info
        val bundle: Bundle? = intent.extras
        val title = bundle?.getString("title")
        val content = bundle?.getString("content")
        val commonIssues = bundle?.getStringArrayList("commonIssues")

        toolbar_title.text = title
        helpContent.text = content

        val n = if (commonIssues == null) 0 else commonIssues?.size
        for (i in 0 until n) {
            val rowTextView = TextView(this)

            @SuppressWarnings
            rowTextView.text = "\u2022 ${commonIssues?.get(i)}"

            var params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // This will define text view width
                LinearLayout.LayoutParams.WRAP_CONTENT // This will define text view heigh
            )

            rowTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
            rowTextView.setTextColor(Color.WHITE)
            rowTextView.typeface = ResourcesCompat.getFont(this, R.font.gibson_light_regular)
            rowTextView.setPadding(5, 5, 5, 5)
            params.setMargins(0,0,0,12)
            rowTextView.layoutParams = params
            linLayCommonIssues.addView(rowTextView)
        }

        stillHaveTroubleTitle.setOnClickListener {
            val intent = Intent(this, NeedMoreHelp::class.java)
//            intent.putExtra("privateWifiNetwork", privateWifiNetwork)
            startActivity(intent)
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
