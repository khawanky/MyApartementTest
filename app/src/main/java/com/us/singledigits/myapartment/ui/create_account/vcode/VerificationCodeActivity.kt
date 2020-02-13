package com.us.singledigits.myapartment.ui.create_account.vcode

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.us.singledigits.myapartment.R
import com.us.singledigits.myapartment.commons.utils.CommonUtils
import com.us.singledigits.myapartment.ui.create_account.password.EnterPasswordActivity
import kotlinx.android.synthetic.main.activity_verificarion_code.*


class VerificationCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verificarion_code)
        handleCodeDigitsEvents()
    }

    fun continueClickEvent(view: View) {
        val enterPasswordActivityIntent = Intent(applicationContext, EnterPasswordActivity::class.java)
        startActivity(enterPasswordActivityIntent)
        finish()
    }

    fun goToLogin(view: View){
        val utils = CommonUtils()
        utils.goToLogin(this)
    }

    // TODO: There is an issue when user backspace then try to rewrite
    fun handleCodeDigitsEvents () {
        etVCodeDigit1.isEnabled = true
        etVCodeDigit1.requestFocus()

        etVCodeDigit1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etVCodeDigit1.length() == 1) {
                    etVCodeDigit2.isEnabled = true
                    etVCodeDigit2.requestFocus()
                    etVCodeDigit1.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable) {
            }
        })

        etVCodeDigit2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etVCodeDigit2.length() == 1) {
                    etVCodeDigit3.isEnabled = true
                    etVCodeDigit3.requestFocus()
                    etVCodeDigit2.isEnabled = false
                }
                if (etVCodeDigit2.length() == 0) {
                    etVCodeDigit2.isEnabled = false
                    etVCodeDigit1.isEnabled = true
                    etVCodeDigit1.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable) {
//                if (etVCodeDigit1.isFocused) {
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.showSoftInput(etVCodeDigit1, InputMethodManager.SHOW_IMPLICIT)
//                }
            }
        })

        etVCodeDigit3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etVCodeDigit3.length() == 1) {
                    etVCodeDigit4.isEnabled = true
                    etVCodeDigit4.requestFocus()
                    etVCodeDigit3.isEnabled = false
                }
                if (etVCodeDigit3.length() == 0) {
                    etVCodeDigit3.isEnabled = false
                    etVCodeDigit2.isEnabled = true
                    etVCodeDigit2.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable) {}
        })

        etVCodeDigit4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etVCodeDigit4.length() == 1) {
                    etVCodeDigit5.isEnabled = true
                    etVCodeDigit5.requestFocus()
                    etVCodeDigit4.isEnabled = false
                }
                if (etVCodeDigit4.length() == 0) {
                    etVCodeDigit4.isEnabled = false
                    etVCodeDigit3.isEnabled = true
                    etVCodeDigit3.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable) {}
        })

        etVCodeDigit5.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etVCodeDigit5.length() == 1) {
                    etVCodeDigit6.isEnabled = true
                    etVCodeDigit6.requestFocus()
                    etVCodeDigit5.isEnabled = false
                } else if (etVCodeDigit5.length() == 0) {
                    etVCodeDigit5.isEnabled = false
                    etVCodeDigit4.isEnabled = true
                    etVCodeDigit4.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        etVCodeDigit6.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etVCodeDigit6.length() === 1) {
                    // TODO: Enable the continue button
                    Toast.makeText(
                        applicationContext,
                        "Enable the continue button",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (etVCodeDigit6.length() === 0) {
                    etVCodeDigit6.isEnabled = false
                    etVCodeDigit5.isEnabled = true
                    etVCodeDigit5.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable) { }
        })
    }


//    fun viewKeyboard() {
//
//
//        val imm: InputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        var view = this.currentFocus
//        if (view == null) {
//            view = View(this)
//        }
//        imm.hideSoftInputFromWindow(view.windowToken, 0)
//
//    }

}
