package com.us.singledigits.myapartment.data.models

import java.io.Serializable

data class MyApartment(
    var siteName:String ="",
    var unitString:String ="",
    var roomString:String ="",
    var fullSiteName:String ="",
    var addressPart1:String ="",
    var addressPart2:String ="",
    var webSite:String ="",
    var manager:String ="",
    var managerEmail:String ="",
    var managerPhone:String =""
) : Serializable