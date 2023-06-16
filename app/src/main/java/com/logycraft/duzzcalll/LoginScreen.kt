package com.logycraft.duzzcalll

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.ValidationUtils
import com.logycraft.duzzcalll.Util.ValidationUtils.isValidNumber
import kotlinx.android.synthetic.main.activity_login_screen.*

class LoginScreen : BaseActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        btnregister.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Terms_And_ConditionActivity::class.java)
                startActivity(intent)
            }

        })
        et_mobile_number.setText("+941234567890")
        et_password.setText("Admin@123")

        btn_login.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (isValidate()) {
                    val intent = Intent(this@LoginScreen, Verify_PhoneActivity::class.java)
                    intent.putExtra("ADMIN", "LOGIN")
                    startActivity(intent)
                }
            }


        })

        btn_forgot_pass.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Edit_PhoneActivity::class.java)
                intent.putExtra("ADMIN", "FORGOT")
                startActivity(intent)
            }

        })
    }

    private fun isValidate(): Boolean {
        val password = et_password.text.toString()
        val mobileNumber = et_mobile_number.text.toString()

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                showError(getString(R.string.enter_mobile_number))
                return false
            }

            TextUtils.isEmpty(password) -> {
                showError(getString(R.string.enter_password))
                return false
            }
            !ValidationUtils.isValidPassword(password) -> {
                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
//                showError(getString(R.string.password_validation_msg))
                return false
            }

            else -> {
                return true
            }
        }
    }
}