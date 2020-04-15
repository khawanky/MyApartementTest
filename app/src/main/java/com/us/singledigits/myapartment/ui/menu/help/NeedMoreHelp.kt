package com.us.singledigits.myapartment.ui.menu.help

import android.os.Bundle
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_need_more_help.*

class NeedMoreHelp : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_need_more_help)
        tvClose.setOnClickListener {
            this.finish()
        }
    }
}
