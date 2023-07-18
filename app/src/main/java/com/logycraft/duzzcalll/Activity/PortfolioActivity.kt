package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restapiidemo.home.data.UserModel
import com.duzzcall.duzzcall.R
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.ValidationUtils
import com.logycraft.duzzcalll.data.SendOTP
import com.duzzcall.duzzcall.databinding.ActivityLoginScreenBinding
import com.duzzcall.duzzcall.databinding.ActivityPortfolioBinding
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import okhttp3.ResponseBody
import org.json.JSONObject


class PortfolioActivity : BaseActivity() {
    //    private lateinit var vm: HomeViewModel
    val userModel = UserModel()
    private lateinit var binding: ActivityPortfolioBinding
    private lateinit var viewModel: HomeViewModel
    var numbers: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_portfolio)
        binding = ActivityPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        vm = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

//        et_first_name.setText("Shageevan")
//        et_last_name.setText("Sachithanandan")
//        et_email.setText("shageevan@gmail.com")
//        et_password.setText("1234567890")
//        numbers = "+94773785342"
        if (intent.getStringExtra("PASS").equals("NEW_PASS")) {
            binding.linearPortfolio.visibility = View.GONE
            binding.viewBottom.visibility = View.GONE
        } else {
            binding.linearPortfolio.visibility = View.VISIBLE
        }


        binding.btnNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (intent.getStringExtra("PASS").equals("NEW_PASS")) {
                    if (isPassValidate()) {
//                        UserCreate()
                    }
                } else {

                    if (isValidate()) {

                        userModel.first_name = binding.etFirstName.text.toString()
                        userModel.last_name = binding.etLastName.text.toString()
                        userModel.email = binding.etEmail.text.toString()
                        userModel.password = binding.etPassword.text.toString()
                        userModel.phone = Preference.getNumber(this@PortfolioActivity)
                        ProgressHelper.showProgrssDialogs(this@PortfolioActivity, "")
                        UserCreate(userModel)
//                        vm.createUser(userModel)
//                        vm.createPostLiveData?.observe(this@PortfolioActivity, Observer {
//                            if (it != null) {
//                                val intent =
//                                    Intent(this@PortfolioActivity, DashboardActivity::class.java)
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent)
//                                overridePendingTransition(
//                                    R.anim.slide_in_right,
//                                    R.anim.slide_out_left
//                                )
//
//                                finish();
//                            } else {
//                                showError("Something Went Wrong!")
//                            }
//                        })

                    }
                }
            }
        })
    }


    private fun UserCreate(userModel: UserModel) {
        viewModel.createUser(userModel)
        viewModel.userLiveData?.observe(this@PortfolioActivity, Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()
                var usedata: SendOTP? = it.data
                Preference.saveToken(
                    this@PortfolioActivity, "Bearer " + usedata?.verificationToken.toString()
                )
                Toast.makeText(this@PortfolioActivity, "" + usedata?.tfaCode, Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this@PortfolioActivity, Verify_PhoneActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish();
                showError("" + usedata?.tfaCode)

            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())

                val keys = jsonObj.keys()
                while (keys.hasNext()) {
                    val key2 = keys.next()
                    val value = jsonObj.optJSONObject(key2)
                    val keys: Iterator<*> = value.keys()

                    while (keys.hasNext()) {
                        val key2 = keys.next()
                        val obj = JSONObject(java.lang.String.valueOf(value))
                        Log.e("Erorrr", "====" + obj.getString(key2.toString()))
                        val responseWithoutBrackets =
                            obj.getString(key2.toString()).removeSurrounding("[\"", "\"]")

                        Toast.makeText(
                            this@PortfolioActivity, "" + responseWithoutBrackets, Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                ProgressHelper.dismissProgressDialog()
                showError("Something Went Wrong!")
            }

        })
    }

    private fun isPassValidate(): Boolean {
        val password = binding.etPassword.text.toString()
        when {
            TextUtils.isEmpty(password) -> {
                showError(getString(R.string.enter_password))
                return false
            }
            !ValidationUtils.isValidPassword(password) -> {
                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun isValidate(): Boolean {

        val firstname = binding.etFirstName.text.toString()
        val lastname = binding.etLastName.text.toString()
        val password = binding.etPassword.text.toString()
        val email = binding.etEmail.text.toString().trim()

        when {
            TextUtils.isEmpty(firstname) -> {
                showError(getString(R.string.enter_firstname))
                return false
            }

            TextUtils.isEmpty(lastname) -> {
                showError(getString(R.string.enter_lastname))
                return false
            }

            TextUtils.isEmpty(email) -> {
                showError(getString(R.string.enter_email_address))
                return false
            }
            !ValidationUtils.isValidEmail(email) -> {
                showError(getString(R.string.invalid_email_address))
                return false
            }

            TextUtils.isEmpty(password) -> {
                showError(getString(R.string.enter_password))
                return false
            }
//            !ValidationUtils.isValidPassword(password) -> {
//                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
//                return false
//            }

            else -> {
                userModel.first_name = firstname
                userModel.last_name = lastname
                userModel.email = email
                userModel.phone = intent.getStringExtra("MOBILE")
                userModel.password = password
                return true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}