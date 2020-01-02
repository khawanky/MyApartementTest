package com.us.singledigits.myapartment.create_account.email

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.CommonUtils
import com.us.singledigits.myapartment.create_account.vcode.VerificationCodeActivity

class EmailCheckerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_checker)
    }

    fun continueClickEvent(view: View) {
        val verificationCodeActivityIntent = Intent(applicationContext, VerificationCodeActivity::class.java)
        startActivity(verificationCodeActivityIntent)
        finish()
    }

    fun goToLogin(view: View){
        val utils = CommonUtils()
        utils.goToLogin(this)
    }
}
