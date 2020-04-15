package com.us.singledigits.myapartment.commons.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.us.singledigits.myapartment.commons.utils.Errors

abstract class BaseViewModel : ViewModel() {
    var toastMessage: MutableLiveData<Errors?>? = MutableLiveData()

    fun handleErrorCodes(errorCode:Int, message:String?) {
        when(errorCode) {
            Errors.BAD_REQUEST.code -> {
                toastMessage?.value = Errors.BAD_REQUEST
            }
            Errors.UNAUTHENTICATED.code -> { // TODO: Go to login page and reset shared preferences
                toastMessage?.value = Errors.UNAUTHENTICATED
            }
            Errors.FORBIDDEN.code -> {
                toastMessage?.value = Errors.FORBIDDEN
            }
            Errors.NOT_FOUNT.code -> {
                toastMessage?.value = Errors.NOT_FOUNT
            }
            Errors.METHOD_NOT_ALLOWED.code -> {
                toastMessage?.value = Errors.METHOD_NOT_ALLOWED
            }
            Errors.MAC_ADDRESS_EXISTS.code -> {
                toastMessage?.value = Errors.MAC_ADDRESS_EXISTS
            }
            Errors.INTERNAL_SERVER.code -> {
                toastMessage?.value = Errors.INTERNAL_SERVER
            }
            else -> {
                if(message == null)
                    toastMessage?.value = Errors.DEFAULT
                else {
                    val error = Errors.DEFAULT
                    error.message = message
                    toastMessage?.value = error
                }
            }
        }
    }


}