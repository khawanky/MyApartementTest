package com.us.singledigits.myapartment.ui.login.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.data.models.Login
import com.us.singledigits.myapartment.data.network.api.MduApi
import com.us.singledigits.myapartment.ui.login.data.model.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(email: String, password: String) {
        MduApi().login(Login(email, password)).enqueue(object :
            Callback<LoggedInUser> {
            override fun onResponse(call: Call<LoggedInUser>, response: Response<LoggedInUser>) {
                if(response.isSuccessful) {
                    Log.d("LOGIN_SUCCESS", "with user id=" + response.body()?.resident?.id)
                    _loginResult.value = LoginResult(success = R.string.login_success,
                        token=response.body()?.token,
                        resident = response.body()?.resident)
                } else {
                    Log.d("LOGIN_FAILED", "With code returned="+response.code())
                    _loginResult.value = LoginResult(error = R.string.login_failed_wrong_credentials)
                }
            }
            override fun onFailure(call: Call<LoggedInUser>, t: Throwable) {
                Log.d("LOGIN_FAILED", t.message)
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        })
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}
