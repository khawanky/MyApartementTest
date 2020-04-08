package com.us.singledigits.myapartment.ui.login.data.model

import com.us.singledigits.myapartment.data.network.responses.ResidentResponse

data class LoggedInUser(
    val token: String?,
    val resident: ResidentResponse?
)
