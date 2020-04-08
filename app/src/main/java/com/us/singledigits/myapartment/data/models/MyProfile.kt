package com.us.singledigits.myapartment.data.models

import java.io.Serializable

data class MyProfile (
    var email:String? ="",
    var firstName:String? ="",
    var lastName:String? ="",
    var phoneNumber:String? =""
) : Serializable