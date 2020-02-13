package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.Site

data class SiteResponse (
    var id:String,
    @SerializedName(value = "attributes")
    var site:Site )