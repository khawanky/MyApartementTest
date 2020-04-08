package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.ResidentLinks
import com.us.singledigits.myapartment.data.models.SiteAttributes

data class SiteResponse (
    var id:Int,
    @SerializedName(value = "attributes")
    var siteAttributes:SiteAttributes,
    var links: ResidentLinks
)