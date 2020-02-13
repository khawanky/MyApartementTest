package com.us.singledigits.myapartment.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.ui.create_account.information.AccountInfoActivity
import com.us.singledigits.myapartment.ui.login.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private val splashTime = 500L
    private lateinit var myHandler:Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // After splash time go to next action
        myHandler = Handler()
        myHandler.postDelayed({afterSplash()},splashTime)
    }

    private fun afterSplash() {
        // TODO: If logged before skip to the proper activity

        // Show content
        buCreate.visibility = View.VISIBLE
        buLogin.visibility = View.VISIBLE
        tvHaveTrouble.visibility = View.VISIBLE
        my_progress_bar.visibility = View.INVISIBLE
    }

    fun createClickEvent(view: View) {
        val createAccountActivityIntent = Intent(applicationContext, AccountInfoActivity::class.java)
        startActivity(createAccountActivityIntent)
        finish()
    }

    fun loginClickEvent(view: View) {
        val loginActivityIntent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(loginActivityIntent)
        finish()
    }

}
