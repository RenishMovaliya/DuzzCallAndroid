package com.logycraft.duzzcalll.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restapiidemo.home.data.UserModel
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.Utils.Companion.FORGOT
import com.logycraft.duzzcalll.Util.Utils.Companion.FROM
import com.logycraft.duzzcalll.Util.Utils.Companion.LOGIN
import com.logycraft.duzzcalll.Util.ValidationUtils
import com.logycraft.duzzcalll.data.LoginData
import com.logycraft.duzzcalll.data.SendOTP
import com.logycraft.duzzcalll.databinding.ActivityIncomingCallBinding
import com.logycraft.duzzcalll.databinding.ActivityLoginScreenBinding
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject
import java.lang.String

class LoginScreen : BaseActivity() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var loggg: LoginData
    private lateinit var binding: ActivityLoginScreenBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login_screen)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding.btnregister.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Terms_And_ConditionActivity::class.java)
////                val intent = Intent(this@LoginScreen, DashboardActivity::class.java)
                startActivity(intent)

            }
        })
        binding.etMobileNumber.setText("+94773499994")
        binding.etMobileNumber.setText("+94773785342")
        binding.etPassword.setText("1234567890")

        binding.btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (isValidate()) {

                    Login(binding.etMobileNumber.text.toString(), binding.etPassword.text.toString())

                }
            }
        })

        binding.btnForgotPass.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent = Intent(this@LoginScreen, Edit_PhoneActivity::class.java)
                intent.putExtra(FROM, FORGOT)
                startActivity(intent)
            }

        })
    }
    var mobileNumber=""
    private fun isValidate(): Boolean {
        val password =  binding.etPassword.text.toString()
         mobileNumber = binding.etMobileNumber.text.toString()

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                showError(getString(R.string.enter_mobile_number))
                return false
            }

            TextUtils.isEmpty(password) -> {
                showError(getString(R.string.enter_password))
                return false
            }
//            !ValidationUtils.isValidPassword(password) -> {
//                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
////                showError(getString(R.string.password_validation_msg))
//                return false
//            }

            else -> {
                return true
            }
        }
    }



    private fun Login(phone: kotlin.String, password: kotlin.String) {
        viewModel.loginUser(phone,password)
        viewModel.loginuserLiveData?.observe(this@LoginScreen, Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()
                var usedata: LoginData? = it.data
                Preference.setLoginData(this@LoginScreen,usedata)
                Preference.saveToken(this@LoginScreen,usedata?.extension?.accessToken)
                val intent = Intent(this@LoginScreen, DashboardActivity::class.java)
//                intent.putExtra(FROM, LOGIN)
//                intent.putExtra("MOBILE", mobileNumber)
                startActivity(intent)
//                showError("" + usedata?.extension?.accessToken)


            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                showError(jsonObj.getString("errors"))

            } else {
                ProgressHelper.dismissProgressDialog()
                showError("Something Went Wrong!")
            }

        })
    }
}