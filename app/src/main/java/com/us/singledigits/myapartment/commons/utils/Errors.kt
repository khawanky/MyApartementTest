package com.us.singledigits.myapartment.commons.utils

enum class Errors (val code:Int, var message:String){
    BAD_REQUEST(400, "Wrong request"),
    UNAUTHENTICATED(401, "Session timeout, login"),
    FORBIDDEN(403, "Call forbidden"),
    NOT_FOUNT(404, "Server not found, or no internet connection"),
    METHOD_NOT_ALLOWED(405, "Method call not allowed"),
    MAC_ADDRESS_EXISTS(409, "Can't add device with existing mac address"),
    INTERNAL_SERVER(500, "Internal error occurred!"),
    DEFAULT(0,"Oops!")
}