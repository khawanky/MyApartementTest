package com.us.singledigits.myapartment.home

class Section {
    var title:String? = null
    var status:String? = null
    var statusIcon:Int? = null

    constructor(title:String, status:String, statusIcon:Int){
        this.title = title
        this.status = status
        this.statusIcon = statusIcon
    }
}