package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.HelpTopicListItem

data class HelpTopicsResponse (
    var status:String,
    @SerializedName(value = "data")
    var helpTopicItems: List<HelpTopicListItem>
)