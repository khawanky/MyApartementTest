package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.DwellingUnitAttributes
import com.us.singledigits.myapartment.data.models.Links

data class DwellingUnitResponse (
    var id:String,
    @SerializedName(value = "attributes")
    var unit:DwellingUnitAttributes,
    var links: Links
)