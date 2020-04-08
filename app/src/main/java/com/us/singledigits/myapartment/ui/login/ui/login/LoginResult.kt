package com.us.singledigits.myapartment.ui.login.ui.login

import com.us.singledigits.myapartment.data.network.responses.ResidentResponse
data class LoginResult(
    val success: Int? = null,
    val error: Int? = null,
    val token:String? = null,
    val resident:ResidentResponse? = null
)
