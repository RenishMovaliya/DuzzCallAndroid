package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restapiidemo.home.data.UserModel
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.ValidationUtils
import kotlinx.android.synthetic.main.activity_portfolio.*
import kotlinx.android.synthetic.main.activity_portfolio.et_password

class PortfolioActivity : BaseActivity() {
//    private lateinit var vm: HomeViewModel
    val userModel = UserModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

//        vm = ViewModelProvider(this)[HomeViewModel::class.java]


        if (intent.getStringExtra("PASS").equals("NEW_PASS")) {
            linear_portfolio.visibility = View.GONE
            view_bottom.visibility = View.GONE
        } else {
            linear_portfolio.visibility = View.VISIBLE
        }


        btn_next.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (intent.getStringExtra("PASS").equals("NEW_PASS")) {
                    if (isPassValidate()) {
                        val intent = Intent(this@PortfolioActivity, DashboardActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish();
                    }
                } else {

                    if (isValidate()) {
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

    private fun isPassValidate(): Boolean {
        val password = et_password.text.toString()
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

        val firstname = et_first_name.text.toString()
        val lastname = et_last_name.text.toString()
        val password = et_password.text.toString()
        val email = et_email.text.toString().trim()

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
            !ValidationUtils.isValidPassword(password) -> {
                showDialogOk(this, "", getString(R.string.password_validation_msg), "OK")
                return false
            }

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