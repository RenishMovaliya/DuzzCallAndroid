package com.logycraft.duzzcalll.Activity

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hbb20.CountryCodePicker
import com.logycraft.duzzcalll.R
import com.logycraft.duzzcalll.Util.BaseActivity
import com.logycraft.duzzcalll.Util.Preference
import com.logycraft.duzzcalll.Util.ProgressHelper.*
import com.logycraft.duzzcalll.Util.Utils.Companion.FORGOT
import com.logycraft.duzzcalll.Util.Utils.Companion.FROM
import com.logycraft.duzzcalll.Util.Utils.Companion.MOBILE
import com.logycraft.duzzcalll.Util.Utils.Companion.OTP
import com.logycraft.duzzcalll.Util.Utils.Companion.VERIFY_TOKEN
import com.logycraft.duzzcalll.data.SendOTP
import com.logycraft.duzzcalll.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_edit_phone.*
import okhttp3.ResponseBody
import org.json.JSONObject

class Edit_PhoneActivity : BaseActivity() {

    lateinit var btn_next: TextView
    lateinit var view_bottom: LinearLayout
    private lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_phone)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        btn_next = findViewById(R.id.btn_next)
        view_bottom = findViewById(R.id.view_bottom)

        if (intent.getStringExtra(FROM).equals(FORGOT)) {
            view_bottom.visibility = View.GONE
        }
        val countryCodePicker = findViewById<CountryCodePicker>(R.id.countyCodePicker)
        val typeface = Typeface.createFromAsset(applicationContext.assets, "font/poppins_light.ttf")
        countryCodePicker.setTypeFace(typeface)
        Tvcountrycode.setText("+" + countryCodePicker.selectedCountryCode)
        countryCodePicker.setOnCountryChangeListener {
            Tvcountrycode.setText("+" + countryCodePicker.selectedCountryCode)
        }


        btn_next.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (isValidate()) {

                    if (intent.getStringExtra(FROM).equals(FORGOT)) {

                        val intent =
                            Intent(this@Edit_PhoneActivity, Verify_PhoneActivity::class.java)
                        intent.putExtra(FROM, intent.getStringExtra(FROM))
                        intent.putExtra(MOBILE, Tvcountrycode.text.toString() + mobileNumber)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    } else {
                        showProgrssDialogs(this@Edit_PhoneActivity, "");
//                        SendOTP(Tvcountrycode.text.toString() + mobileNumber);
                        Preference.saveNumber(
                            this@Edit_PhoneActivity, Tvcountrycode.text.toString() + mobileNumber
                        )
                        val intent = Intent(this@Edit_PhoneActivity, PortfolioActivity::class.java)
//                        intent.putExtra(MOBILE, Tvcountrycode.text.toString() + mobileNumber)
                        intent.putExtra(FROM, intent.getStringExtra(FROM))
//                        intent.putExtra(OTP, sendOtp?.tfaCode.toString())
////                VERIFY_TOKEN = Preference.getToken(this@Edit_PhoneActivity).toString()
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    }
                }


            }

        })
    }

    private fun SendOTP(phone: String) {


        viewModel.sentOtp(Tvcountrycode.text.toString() + mobileNumber)
        viewModel.sentOtpLiveData?.observe(this@Edit_PhoneActivity, Observer {

            if (it.isSuccess == true && it.Responcecode == 200) {
                dismissProgressDialog()
                var sendOtp: SendOTP? = it.data
                Preference.saveToken(
                    this@Edit_PhoneActivity, "Bearer " + sendOtp?.verificationToken.toString()
                )
                Preference.saveNumber(
                    this@Edit_PhoneActivity, Tvcountrycode.text.toString() + mobileNumber
                )
                val intent = Intent(this@Edit_PhoneActivity, Verify_PhoneActivity::class.java)
                intent.putExtra(MOBILE, Tvcountrycode.text.toString() + mobileNumber)
                intent.putExtra(FROM, intent.getStringExtra(FROM))
                intent.putExtra(OTP, sendOtp?.tfaCode.toString())
//                VERIFY_TOKEN = Preference.getToken(this@Edit_PhoneActivity).toString()
                Toast.makeText(
                    this@Edit_PhoneActivity, sendOtp?.tfaCode.toString(), Toast.LENGTH_LONG
                ).show()
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else if (it.error != null) {
                dismissProgressDialog()
                var errorResponce: ResponseBody = it.error
                val jsonObj = JSONObject(errorResponce!!.charStream().readText())
                showError(jsonObj.getString("errors"))
            } else {
                dismissProgressDialog()
                showError("Something Went Wrong!")
            }

        })
    }


    var mobileNumber = "";
    private fun isValidate(): Boolean {
        mobileNumber = et_mobile.text.toString()

        when {
            TextUtils.isEmpty(mobileNumber) -> {
                showError(getString(R.string.enter_mobile_number))
                return false
            }
            else -> {
                return true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}