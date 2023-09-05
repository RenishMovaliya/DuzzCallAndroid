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
import com.google.gson.Gson
import com.google.gson.JsonElement
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
                    if (isValidate()) {

                        ProgressHelper.showProgrssDialogs(this@PortfolioActivity)
                        UserCreate()
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
        })
    }


    private fun UserCreate() {

        var element: JsonElement? = null
        val `object` = JSONObject()
        `object`.put("first_name", binding.etFirstName.text.toString())
        `object`.put("last_name", binding.etLastName.text.toString())
        `object`.put("email", binding.etEmail.text.toString())
        element = Gson().fromJson(`object`.toString(), JsonElement::class.java)


        viewModel.createUser(element,this@PortfolioActivity)
        viewModel.userLiveData?.observe(this@PortfolioActivity, Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                ProgressHelper.dismissProgressDialog()
                var usedata: JsonElement? = it.data

//                Toast.makeText(this@PortfolioActivity, "" + usedata?.tfaCode, Toast.LENGTH_LONG)
//                    .show()
                Preference.setFirstUser(this@PortfolioActivity, true)

                val intent = Intent(this@PortfolioActivity, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish();

            } else if (it.error != null) {
                ProgressHelper.dismissProgressDialog()
//                var errorResponce: ResponseBody = it.error
//                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                showError("Something went Wrong!")

//                val keys = jsonObj.keys()
//                while (keys.hasNext()) {
//                    val key2 = keys.next()
//                    val value = jsonObj.optJSONObject(key2)
//                    val keys: Iterator<*> = value.keys()
//
//                    while (keys.hasNext()) {
//                        val key2 = keys.next()
//                        val obj = JSONObject(java.lang.String.valueOf(value))
//                        Log.e("Erorrr", "====" + obj.getString(key2.toString()))
//                        val responseWithoutBrackets =
//                            obj.getString(key2.toString()).removeSurrounding("[\"", "\"]")
//
//                        Toast.makeText(
//                            this@PortfolioActivity, "" + responseWithoutBrackets, Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
            } else {
                ProgressHelper.dismissProgressDialog()
                showError("Something Went Wrong!")
            }

        })
    }


    private fun isValidate(): Boolean {

        val firstname = binding.etFirstName.text.toString()
        val lastname = binding.etLastName.text.toString()
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

            else -> {
                userModel.first_name = firstname
                userModel.last_name = lastname
                userModel.email = email
                userModel.phone = intent.getStringExtra("MOBILE")
                return true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}