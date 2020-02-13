package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.Resident

data class ResidentResponse (
    var id:String,
    @SerializedName(value = "attributes")
    var resident:Resident )