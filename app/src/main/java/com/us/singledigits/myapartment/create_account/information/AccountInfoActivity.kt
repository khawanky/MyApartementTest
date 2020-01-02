package com.us.singledigits.myapartment.create_account.information

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.CommonUtils
import com.us.singledigits.myapartment.create_account.email.EmailCheckerActivity

class AccountInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)
    }

    fun continueClickEvent(view: View) {
        val emailCheckerActivityIntent = Intent(applicationContext, EmailCheckerActivity::class.java)
        startActivity(emailCheckerActivityIntent)
        finish()
    }

    fun goToLogin(view: View){
        val utils = CommonUtils()
        utils.goToLogin(this)
    }

}
