package com.us.singledigits.myapartment.data.network.responses

import com.google.gson.annotations.SerializedName
import com.us.singledigits.myapartment.data.models.HelpTopicListItem
import com.us.singledigits.myapartment.data.models.Site

data class HelpTopicsResponse (
    @SerializedName(value = "data")
    var helpTopicItems: List<HelpTopicListItem>
)