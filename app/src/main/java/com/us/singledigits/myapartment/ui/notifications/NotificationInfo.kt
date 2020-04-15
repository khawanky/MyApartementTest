package com.us.singledigits.myapartment.ui.notifications

import android.graphics.Typeface

class NotificationInfo (
    var id:Int = 0,
    var icon:Int = 0,
    var isProperty:Boolean = false,
    var isNew:Boolean = false,
    var font: Typeface? = null,
    var time:String = "",
    var title:String = "",
    var content:String = ""
)