package com.us.singledigits.myapartment.data.models

import java.io.Serializable

data class DeviceFromSocket(
    var deviceID: String,
    var attributeType: String,
    var value: String
) : Serializable