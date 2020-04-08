package com.us.singledigits.myapartment.ui.notifications

data class NotificationInfo (
    var id:Int? = 0,
    var icon:Int? = 0,
    var isProperty:Boolean? = false,
    var isNew:Boolean? = false,
    var time:String? = "",
    var title:String? = "",
    var content:String? = ""
)