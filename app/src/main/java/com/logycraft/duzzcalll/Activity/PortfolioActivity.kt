package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restapiidemo.home.data.UserModel
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper
import com.logycraft.duzzcalll.Util.ValidationUtils
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_portfolio.*
import okhttp3.ResponseBody
import org.json.JSONObject


class PortfolioActivity : BaseActivity() {
    //    private lateinit var vm: HomeViewModel
    val userModel = UserModel()
    private lateinit var viewModel: HomeViewModel
    var numbers: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)

//        vm = ViewModelProvider(this)[HomeViewModel::class.java]

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        et_first_name.setText("Shageevan")
        et_last_name.setText("Sachithanandan")
        et_email.setText("shageevan@gmail.com")
        et_password.setText("1234567890")
        numbers = "+94773785342"
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
//                        UserCreate()
                    }
                } else {

                    if (isValidate()) {

                        userModel.first_name = et_first_name.text.toString()
                        userModel.last_name = et_last_name.text.toString()
                        userModel.email = et_email.text.toString()
                        userModel.password = et_password.text.toString()
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
//                var usedata: JsonElement? = it.data
                val intent = Intent(this@PortfolioActivity, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish();
//                showError("" + usedata.toString())


            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
//                val keys = jsonObj.keys()
//                val value = targetJson.optString(keys.)



                val keys = jsonObj.keys()

                    while (keys.hasNext()) {
                        val key2 = keys.next()
                        val value = jsonObj.optJSONObject(key2)
                        val keys: Iterator<*> = value.keys()

                        while(keys.hasNext()) {
                            val key2 = keys.next()
                            Log.e("Erorrr", key2 as String)

                        }


//                        while (value.keys().hasNext()) {
//
//                            val key2 = value.keys().next()
//                            val value = jsonObj.optString(key2)
//                            showError(value.toString())
//
//                        }
                    }

//                val iter: Iterator<String> = jsonObj.keys()
//                while (iter.hasNext()) {
//                    val key = iter.next()
//                    try {
//
//                        val value: Any = jsonObj.get(key)
//                        showError(value.toString())
//                    } catch (e: JSONException) {
//                        // Something went wrong!
//                    }
//                }
//                showError(jsonObj.getString("errors"))
            } else {
                ProgressHelper.dismissProgressDialog()
                showError("Something Went Wrong!")
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