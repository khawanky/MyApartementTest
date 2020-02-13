package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.DwellingUnitRoom

data class DwellingUnitRoomResponse (
    var id:String,
    @SerializedName(value = "attributes")
    var room:DwellingUnitRoom
)