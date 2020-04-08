package com.us.singledigits.myapartment.data.network.responses

import com.us.singledigits.myapartment.data.models.Notification

data class NotificationsResponse (
    var hasUnreadNotifications:Boolean,
    var data:List<Notification>)