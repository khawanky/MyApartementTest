package com.us.singledigits.myapartment.ui.tvguide

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.toolbar_with_backarrow.*

class TvguideActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tvguide)
        // Action bar
        setSupportActionBar(backActionBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title.setText(R.string.tvGuide)

        val wv = findViewById<WebView>(R.id.wvTvChannels)
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }

        wv.settings.javaScriptEnabled = true
        wv.settings.allowContentAccess = true
        wv.settings.allowFileAccess = true
        wv.settings.databaseEnabled = true
        wv.settings.domStorageEnabled = true
//        wv.loadUrl("https://www.google.co.in/")

        wv.loadUrl("https://mtvl.zap2it.com/tvlistings/ZCGrid.do?method=decideFwdForLineup&zipcode=60804&lineupId=ECHOST:-")
//        wv.loadUrl("https://tvlistings.zap2it.com/?aid=gapzap")
//        wv.loadUrl("google.com.eg")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
