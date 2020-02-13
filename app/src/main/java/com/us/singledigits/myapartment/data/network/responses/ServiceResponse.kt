package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.Resident
import com.us.singledigits.myapartment.data.models.Service

data class ServiceResponse (
    var data:List<Service>
)