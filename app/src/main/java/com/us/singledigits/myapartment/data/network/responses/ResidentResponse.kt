package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.ResidentAttributes
import com.us.singledigits.myapartment.data.models.ResidentLinks

data class ResidentResponse (
    var id:Int,
    @SerializedName(value = "attributes")
    var residentAttributes:ResidentAttributes,
    var links: ResidentLinks
)