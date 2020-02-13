package com.us.singledigits.myapartment.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DwellingUnitDevice(
    var id: String,
    @SerializedName(value = "attributes")
    var device: DwellingUnitDeviceAttributes,
    var deviceStatus: ArrayList<DeviceFromSocket>
) : Serializable