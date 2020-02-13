package com.us.singledigits.myapartment.ui.create_account.password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.CommonUtils

class EnterPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_password)
    }

    fun finishEvent(view: View){
        // TODO: go to homepage
        val utils = CommonUtils()
        utils.goToLogin(this)
    }

    fun goToLogin(view: View){
        val utils = CommonUtils()
        utils.goToLogin(this)
    }
}
