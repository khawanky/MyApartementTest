package com.us.singledigits.myapartment.ui.home

import com.us.singledigits.myapartment.data.models.DwellingUnitDevice

class HomeTileItem (
    var title:String,
    var status:String,
    var statusColor:Int,
    var level:String,
    var statusIconResource: Int,
    var tileName: String,
    var isEnabled: Boolean,
    var iotTileDevices: List<DwellingUnitDevice>?
)
