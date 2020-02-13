package com.us.singledigits.myapartment.data.models

import com.google.gson.annotations.SerializedName

data class HelpTopicListItem (
    var id:String,
    @SerializedName(value = "attributes")
    var helpTopic:HelpTopic
)