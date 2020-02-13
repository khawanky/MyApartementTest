package com.us.singledigits.myapartment.data.models

data class Resident (
    var emailAddress:String,
    var password:String,
    var firstName:String,
    var lastName:String,
    var phoneNumber:String,
    var leaseStartDate:String,
    var leaseEndDate:String,
    var isRegistered:Boolean )