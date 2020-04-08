package com.us.singledigits.myapartment.data.models

data class NotificationAttributes (
    var type:String,
    var recipient:String,
    var title:String,
    var content:String,
    var sentAt:String,
    var seenAt:String )