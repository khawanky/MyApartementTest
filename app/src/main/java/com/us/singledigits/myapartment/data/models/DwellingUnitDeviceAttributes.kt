package com.us.singledigits.myapartment.data.models

import java.io.Serializable

data class DwellingUnitDeviceAttributes (
    var platformName:String,
    var name:String,
    var platformIdentifier:String,
    var platformHubIdentifier:String,
    var function:String
) : Serializable