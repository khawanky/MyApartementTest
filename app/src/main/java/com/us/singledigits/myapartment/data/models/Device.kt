package com.us.singledigits.myapartment.data.models

data class Device (
    var id:Int,
    var attributes:DeviceAttribute,
    var isPersonal:Boolean = false
)