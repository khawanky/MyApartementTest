package com.us.singledigits.myapartment.ui.devices

data class DeviceInfo (
    var deviceId:Int = 0,
    var name:String = "",
    var status:String = "",
    var macAddress:String = "",
    var deviceIcon:Int = 0,
    var isPersonal:Boolean = false
)
